package org.phylotastic.mapreducepruner;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import org.phylotastic.mrppath.*;


/** 
 * class: Pass3Mapper
 * -------------------------------------------------------------------------
 * 
 * Mapper class, an element of the Hadoop MapReduce framework
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class MrpPass3Mapper extends Mapper<Text, Text, Text, Text> 
{
    private static final Text IDtext            = new Text("(=)");    
    private static final Logger logger          = Logger.getLogger(MrpPass3Mapper.class.getName());
    
    private Configuration jobConf;        // the hadoop job configuration
    
    /**
     *     method: setup
     * 
     *     This method is called once for each mapper task. So if 10 mappers
     *     were spawned for a job, then for each of those mappers it will be
     *     called once
     *     General guideline is to add any task that is required to be done
     *     only once, like getting the path of the distributed cache,
     *     passing and getting parameters to mappers, etc.
     * 
     *     The method has no real function here yet; it is added for completeness
     *
     * @param context   a Hadoop context, giving access to data related to the pass1 job
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void setup(Context context) throws IOException, InterruptedException
    {
        super.setup(context);        
        this.jobConf = context.getConfiguration();
    }

    /** 
     *     method: map
     * 
     *     Map-3 step prepares the data for the names to be re-attaches to
     *     their respective tip nodes and for any remainig internal nodes that
     *     subtend only 1 tip, to be removed.
     *
     *     For example, for tree
     * 
     *             (n1)                           A        C   D
     *             /  \                            \      /   /
     *           (n2)  \                           (n4)  /   /
     *           /  \   \                            \  /   /
     *         (n3)  \   \                           (n3)  /
     *         /  \   \   \                            \  /
     *       (n4)  \   \   \                           (n2)
     *       /  \   \   \   \
     *      A    B   C   D   E
     *
     *     if taxons are A and C and D, Map-3() receives the records:
     *     (=)          A:Agoracea
     *     (=)          C:Catonacea
     *     (=)          D:Draconacea
     *     A            n4,1
     *     A,C          n3,2
     *     A,C,D        n2,3
     *
     *     in no perticular following order
     *
     *     processing will:
     *     - split the tipsets into ID/node pairs
     *     - split the name-item into an ID/name pair
     *
     *     after processing Map-3() emits the records:
     *     labelOf(A)   A:Agoracea
     *     labelOf(C)   C:Catonacea
     *     labelOf(D)   D:Draconacea
     *     labelOf(A)   n2,3
     *     labelOf(C)   n2,3
     *     labelOf(D)   n2,3
     *     labelOf(A)   n3,3
     *     labelOf(C)   n3,3
     *     labelOf(A)   n4,1
     * 
     *     instead of the tipNode.toString() value, now the integer value of tipNode.getLabel()
     *     is used as the key-part of the map output. node.toString() is the value-part.
     *     This is not a necessity for the process, but the label's integer value prefixed
     *     with zeroes to 6 positions (String.format("%06d", node.getLabel())) should give
     *     a cleaner sort result and therefore an easier to (manually) check output.
     *     It might also be of help for further processing of the result into a tree.
     *
     *     Before being presented to the Reduce-3() method, the emited records are sorted, giving
     *     A    n2,3
     *     A    n3,2
     *     A    n4,1
     *     A    =A:Agoracea
     *     C    n2,3
     *     C    n3,2
     *     C    =C:Catonacea
     *     D    n2,3
     *     D    =D:Draconacea
     * 
     *     where it says A, C and D the integer labels of those nodes are meant.
     *     like in the case of the parkia:
     *     "000628       623:1"
     *     "000628       625:1"
     *     "000628       =628:18:parkia"
     * 
     * @param tipText   the tipset or IDtext
     * @param nodeText  the internal node (+count)
     * @param context   the Hadoop output context for writing the results
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Override
    public void map(Text tipText, Text nodeText, Context context) throws IOException, InterruptedException
    {
        String tipString = tipText.toString();
        String nodeString = nodeText.toString();
        if (tipText.equals(IDtext)) {
            /*     it is a tip name record like:
             *     "(=)		628:18:parkia"
             *     write out exchanging IDtext with the tip's label
             *     "000628      628:18:parkia"
             */
//            logger.info("Map: input = " + "(=)" + "\t:\t" + nodeString);
            PathNode tipNode = PathNode.fromString(nodeString);
            /* get a string representation of the label,
             * prefixed with zeroes to 6 positions */
            String tipLabel = String.format("%06d", tipNode.getLabel());
            /* write the new name record */
            context.write(new Text(tipLabel), new Text("=" + nodeString));
//            logger.info("Map: output = " + tipLabel + "\t:\t" + nodeString);
        } else {
            /*     it is a tip set like:
             *     A,C,D	n2,3
             *     write out as seperate tip/node pairs; e.g.
             *     labelOf(A)	n2,3
             *     labelOf(C)	n2,3
             *     labelOf(D)	n2,3
             *     where only the tip's 6 position label represents the tip
             *     like in the case of the parkia:
             *     "000628       623:1"
             *     "000628       625:1"
             */
//            logger.info("Map: input = " + tipString + "\t:\t" + nodeString);
            /* split line into tipSet and ancestor */
            PathNodeSet tipSet = PathNodeSet.fromString(tipString);
            /* for each tip write the record. */
            for ( PathNode tipNode : tipSet.getSet() ) {
                String tipLabel = String.format("%06d", tipNode.getLabel());
                context.write(new Text(tipLabel), nodeText);
//                logger.info("Map: output = " + tipLabel + "\t:\t" + nodeString);
            }
        }
    }
    
}
