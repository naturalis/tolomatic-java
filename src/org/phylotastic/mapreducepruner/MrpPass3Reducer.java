package org.phylotastic.mapreducepruner;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import org.phylotastic.mrppath.*;

/** 
 * class: Pass3Reducer
 * -------------------------------------------------------------------------
 * 
 * Reducer class, an element of the Hadoop MapReduce framework
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class MrpPass3Reducer extends Reducer<Text, Text, Text, Text>
{
    private static final Text IDtext            = new Text("(=)");    
    private static final Logger logger          = Logger.getLogger(MrpPass3Reducer.class.getName());
    
    private Configuration jobConf;        // the hadoop job configuration
    
    /**
     *     method: setup
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
     *     if taxons are A and C and D, Reduce-3() receives:
     *     A    {n2,3;n3,2;n4,1;=A:Agoracea}    // {} = iterable list
     *     C    {n2,3;n3,2;=C:Catonacea}        // {} = iterable list
     *     D    {n2,3;=D:Draconacea}            // {} = iterable list
     *
     *     where it lists the keys A, C and D the integer labels of those nodes are meant.
     * 
     *     processing will:
     *     - assemble the path "per tip"
     *     - remove internal nodes that only subtend 1 tip
     *       adding their branch length to that of the tip
     *
     *     after processing Reduce-3() emits the records:
     *     A:Agoracea           n2,n3
     *     C:Catonacea          n2,n3
     *     D:Draconacea         n2
     *
     *     Representing the tree
     * 
     *          (n2)                            A        C   D
     *          /  \                             \      /   /
     *        (n3)  D:Draconacea                  \    /   /
     *        /  \                                 \  /   /
     *       /    C:Catonacea                      (n3)  /
     *      /                                        \  /
     *     A:Agoracea                                (n2)
     *
     *     further processing can then result in the Newick representation for the tree:
     * 
     * @param tipText   the key of the taxons external node
     * @param nodes     (all) the nodes on the taxons path
     * @param context   the Hadoop output context for writing the results
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Override
    public void reduce(Text tipText, Iterable<Text> nodes, Context context) throws IOException, InterruptedException
    {
        /*     this is a taxon node set like this:
         *     A        n2,3; n3,2; n4,1 and =A:Agoracea
         *     where A is the external node's label tipText
         *     and n2,n3,n4, and =A:Agoracea are the taxon's path nodes
         *     like in the case of the parkia:
         *     "000628      623:1, 625:1 and =628:18:parkia"
         */
        PathNode taxonTip = null;
        PathNodeInternal taxonNode = null;
        PathNodeSet taxonPath = new PathNodeSet();
        int taxonLength = 0;
        for(Text node : nodes)
        {
            String nodeString = node.toString();
//            logger.info("reduce: input = " + tipText.toString() + "\t:\t" + nodeString);
            if (nodeString.startsWith("=")) {
                /* it is the name node */
                taxonTip = PathNode.fromString(nodeString.substring(1));
                taxonLength += taxonTip.getLength();
            } else {
                /* it is an internal node; neglect those only subtending
                 * one tip; add their length to that of the tip node  */
                taxonNode = PathNodeInternal.fromString(nodeString);
                int tipCount = taxonNode.getTipCount();
                if (tipCount == (int)1)
                    taxonLength += tipCount;
                else
                    taxonPath.addNode(new PathNode(taxonNode.getLabel(), taxonNode.getLength()));
            }
        }
        /* adjust the taxons branch length */
        taxonTip.setLength(taxonLength);
        /* write the result: e.g.
         * "628:18:parkia   ..., 622:1, 623:1, ..." */
        context.write(new Text(taxonTip.toString()), new Text(taxonPath.toString()));
//        logger.info("Reduce: output = " + taxonTip.toString() + "\t:\t" + taxonPath.toString());
    }
    
}
