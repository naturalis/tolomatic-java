package org.phylotastic.mapreducepruner;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import org.phylotastic.mrppath.*;

/** 
 * class: Pass1Reducer
 * -------------------------------------------------------------------------
 * 
 * a Reducer class, an element of the Hadoop MapReduce framework
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 * 
 */
public class MrpPass1Reducer extends Reducer<Text, Text, Text, Text>
{
    private static final Text IDtext            = new Text("(=)");    
    private static final Logger logger          = Logger.getLogger(MrpPass1Reducer.class.getName());
    
    private Configuration jobConf;        // the hadoop job configuration
    
    /**
     * method: setup
     * 
     *     This method is called once for each reducer task. So if 10 reducers
     *     were spawned for a job, then for each of those reducers it will be
     *     called once
     *     General guideline is to add any task that is required to be done
     *     only once, like getting the path of the distributed cache,
     *     passing and getting parameters to reducers, etc.
     * 
     *     The method has no real function here yet; it is added for completeness
     *
     * @param context   a Hadoop context, giving access to data related to the pass1 job
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void setup(Reducer.Context context) throws IOException, InterruptedException
    {
        super.setup(context);         
        this.jobConf = context.getConfiguration();
    }
    
    /** 
     *     method: reduce
     *     ---------------------------------------------------------------------
     * 
     *     Given a node ID (as described for "map") as a key, and all the tips that have
     *     that node on their respective paths to the root, this method combines these to
     *     emit a concatenated list of all tips, then the node ID, then the tip count.
     * 
     *     For example, for tree
     *
     *             (n1)                           A        C   D
     *             /  \	                           \      /   /
     *           (n2)  \                           (n4)  /   /
     *           /  \   \                            \  /   /
     *         (n3)  \   \                           (n3)  /
     *         /  \   \   \                            \  /
     *       (n4)  \   \   \                           (n2)
     *       /  \   \   \   \                            \
     *      A    B   C   D   E                          (n1)
     *
     *     if taxons are A and C and D, Reduce-1() receives the records:
     *     (=)  {A:Agoracea,C:Catonacea,D:Draconacea}   // {} = iterable list
     *     n1   {A,C,D}                                 // {} = iterable list
     *     n2   {A,C,D}                                 // {} = iterable list
     *     n3   {A,C}                                   // {} = iterable list
     *     n4   {A}                                     // {} = iterable list
     *
     *     after processing Reduce-1() emits the records:
     *     (=)          A:Agoracea
     *     (=)          C:Catonacea
     *     (=)          D:Draconacea
     *     A,C,D        n1,3
     *     A,C,D        n2,3
     *     A,C          n3,2
     *     A            n4,1
     * 
     * @param node      the internal node
     * @param nodeTips  the external nodes with "node" in their path
     * @param context   the Hadoop output context for writing the results
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Override
    public void reduce(Text node, Iterable<Text> nodeTips, Context context) throws IOException, InterruptedException
    {
        if (node.equals(IDtext)) {
            /*     then it is a taxon name set like this:
             *     "(=)	{A:Agoracea,C:Catonacea,D:Draconacea}
             *     where (=) is the node
             *     and A:Agoracea, C:Catonacea and D:Draconacea are the nodeTips
             *     write out without further processing: e.g.
             *     (=)          A:Agoracea
             *     (=)          C:Catonacea
             *     (=)          D:Draconacea
             */
            for (Text taxonID : nodeTips) {
//                logger.info("Reduce: input = " + "(=)" + "\t:\t" + taxonID.toString());
                context.write(node, taxonID);
//                logger.info("Reduce: output = " + "(=)" + "\t:\t" + taxonID.toString());
            }
        } else {
            /*     it is a set of taxon nodes like this:
             *     "n1 {A,C,D}"
             *     where n1 is the internal node
             *     and A,C and D are the external nodeTips it opposes
             *
             *     write a counted version of taxon node set like this:
             *     A,C,D       n1,3
             *     read the internal node's data
             */
            PathNode internalNode = new PathNode(node.toString());
            /* read the nodeTips into a sorted list of tipnodes
             * (i.e. into a pathNodeSet) */
            PathNodeSet tipSet = new PathNodeSet();
            for (Text nodeTip : nodeTips) {
                tipSet.addNode(new PathNode(nodeTip.toString()));
//                logger.info("Reduce: input = " + node.toString() + "\t:\t" + nodeTip.toString());
            }
            /* create an internal node with a count */
            PathNodeInternal countedInternalNode = 
                    new PathNodeInternal(internalNode, tipSet.getSize());
//            logger.info("Reduce: output = " + node.toString() + "\t:\t" + 
//                    tipSet.toString() + " : " + countedInternalNode.toString());
            /* write out the counted node set as one record. */
            context.write(new Text(tipSet.toString()), new Text(countedInternalNode.toString()));
        }
    }
    
}
