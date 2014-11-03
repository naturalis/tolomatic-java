package org.phylotastic.mapreducepruner;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

/** class: Pass2Mapper
 * -------------------------------------------------------------------------
 * 
 * Mapper class, an element of the Hadoop MapReduce framework
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class MrpPass2Mapper extends Mapper<Text, Text, Text, Text>
{
    private static final Text IDtext            = new Text("(=)");    
    private static final Logger logger          = Logger.getLogger(MrpPass2Mapper.class.getName());
    
    private Configuration jobConf;        // the hadoop job configuration
    
    /**
     *     method: setup
     *     -------------------------------------------------------------------------
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
     *     -------------------------------------------------------------------------
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
     *       /  \   \   \   \                            \
     *      A    B   C   D   E                           (n1)
     *
     *     if taxons are A and C and D, Map-2() receives the records:
     *     (=)          A:Agoracea
     *     (=)          C:Catonacea
     *     (=)          D:Draconacea
     *     A,C,D        n1,3
     *     A,C,D        n2,3
     *     A,C          n3,2
     *
     *     in no perticular following order
     *
     *     Map2 does not do any processing; it is only there
     *     to enable the Reduce2 process
     *
     *     after processing Map-2() emits the records:
     *     (=)          A:Agoracea
     *     (=)          C:Catonacea
     *     (=)          D:Draconacea
     *     A,C,D        n1,3
     *     A,C,D        n2,3
     *     A,C          n3,2
     *     A            n4,1
     *
     *     Before being presented to the Reduce-2() method, the emited records are sorted, giving
     *     (=)          A:Agoracea
     *     (=)          C:Catonacea
     *     (=)          D:Draconacea
     *     A            n4,1
     *     A,C          n3,2
     *     A,C,D        n1,3
     *     A,C,D        n2,3
     * 
     *     input format = key-value pairs !!
     * 
     * @param tipSet    the tipset or IDtext
     * @param node      the internal node(+ node count)
     * @param context   the Hadoop output context for writing the results
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Override
    public void map(Text tipSet, Text node, Context context) throws IOException, InterruptedException
    {
        String tipString = tipSet.toString();
        if (tipSet.equals(IDtext)) {
            /*     it is a tip name record like:
             *     "(=)         A:Agoracea"
             *     write out without further processing: e.g.
             *     (=)          A:Agoracea
             */
//            logger.info("Map: input = " + "(=)" + "\t:\t" + node.toString());
            context.write(tipSet, node);
//            logger.info("Map: output = " + "(=)" + "\t:\t" + node.toString());
        } else {
            /*     it is a counted tip set like:
             *     A,C,D        n1,3
             *     write out without further processing: e.g.
             *     A,C,D        n1,3
             */
            String nodeString = node.toString();
//            logger.info("Map: input = " + tipString + "\t:\t" + nodeString);
            context.write(tipSet, node);
//            debugLogger.debug("Map: output = " + tipString + "\t:\t" + nodeString);
        }            
    }
    
}
