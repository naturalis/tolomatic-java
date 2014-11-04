package org.phylotastic.mapreducepruner;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

import org.phylotastic.mrppath.*;


/** 
 * class: Pass2Reducer
 * -------------------------------------------------------------------------
 * 
 * Reducer class, an element of the Hadoop MapReduce framework
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class MrpPass2Reducer extends Reducer<Text, Text, Text, Text>
{
    private static final Text IDtext            = new Text("(=)");    
    private static final Logger logger          = Logger.getLogger(MrpPass2Reducer.class.getName());
    
    private Configuration jobConf;        // the hadoop job configuration
    
    /**
     *     method: setup
     *     -------------------------------------------------------------------------
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
     *     -------------------------------------------------------------------------
     * 
     *     Given a concatenated list of tips, a node ID and the number of tips it subtends,
     *     ( like: A|B,n1,2).
     *     In cases where a concatenated list of tips has multiple internal nodes
     *     associated with it, this means that there are unbranched internal nodes on
     *     the path from those tips to the root. Of those unbranched internals we want the
     *     MRCA of the tips, which we obtain by sorting the node IDs: since these are
     *     applied in pre-order, the highest node ID in the list is the MRCA.
     *     (MRCA = Most recent common ancestor)
     * 
     *     For example, for tree
     * 
     *             (n1)                           A        C   D
     *             /  \	                       \      /   /
     *           (n2)  \                           (n4)  /   /
     *           /  \   \                            \  /   /
     *         (n3)  \   \                           (n3)  /
     *         /  \   \   \                            \  /
     *       (n4)  \   \   \                           (n2)
     *       /  \   \   \   \                            \
     *      A    B   C   D   E                           (n1)
     *
     *     if taxons are A and C and D, Reduce() receives the records:
     *     (=)          {A:Agoracea,C:Catonacea,D:Draconacea}   // {} = iterable list
     *     A            {n4,1}                                  // {} = iterable list
     *     A,C          {n3,2}                                  // {} = iterable list
     *     A,C,D        {n1,3; n2,3}                            // {} = iterable list
     *
     *     processing will:
     *     - neglect unbranched nodes in multi-node tiplist		=> like n1 for A,C,D
     *       and regard only the MRCA (Most recent common ancestor)	=> like n2 for A,C,D
     *     - add the branche length of neglected nodes to
     *       that of their MRCA
     *
     *     after processing Reduce-2() emits the records:
     *     (=)          A:Agoracea
     *     (=)          C:Catonacea
     *     (=)          D:Draconacea
     *     A            n4,1
     *     A,C          n3,2
     *     A,C,D        n2,3
     *
     *     Representing the tree
     * 
     *           (n2)                            A        C   D
     *           /  \                             \      /   /
     *         (n3)  \                            (n4)  /   /
     *         /  \   \                             \  /   /
     *       (n4)  \   \                            (n3)  /
     *       /      \   \                             \  /
     *      A        C   D                            (n2)
     * 
     * @param tipText   the tipset or IDtext
     * @param nodes     the internal nodes that are on the path of the tipset
     * @param context   the Hadoop output context for writing the results
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Override
    public void reduce(Text tipText, Iterable<Text> nodes, Context context) throws IOException, InterruptedException
    {
        if (tipText.equals(IDtext)) {
            /*     it is a taxon name set like this:
             *     "(=)         {A:Agoracea,C:Catonacea,D:Draconacea}
             *     where (=) is in tipText
             *     and A:Agoracea, C:Catonacea and D:Draconacea are the nodes
             *     write out without further processing: e.g.
             *     (=)          A:Agoracea
             *     (=)          C:Catonacea
             *     (=)          D:Draconacea
             */
            for (Text taxonID : nodes) {
//                logger.info("Reduce: input = " + "(=)" + "\t:\t" + taxonID.toString());
                context.write(tipText, taxonID);
//                logger.info("Reduce: output = " + "(=)" + "\t:\t" + taxonID.toString());
            }
        } else {
            /*     it is a set of counted tip sets like:
             *     A,C          {n3,2}
             *     A,C,D        {n1,3; n2,3}
             *     processing will:
             *     neglect unbranched nodes in multi-node tiplist, like n1 for A,C,D
             *     regard only the MRCA (Most recent common ancestor), like n2 for A,C,D
             *     The MRCA is the one with the highest ID/Label; in this case n2
             *     so the example would write:
             *     A,C          n3,2
             *     A,C,D        n2,3
             *     adding the length of neglected unbrached nodes to the
             *     length of the MRCA.
             */
            double accumulatedBranchLengths = 0;
            int nearestNodeId = 0;
            int tipCount = 0;
            /*     find the MRCA whitch is the one with the highest ID (= label)
             *     like n2 in: "A,C,D    {n1,3; n2,3}"
             *     or the only one there is, like n3 in: "A,C    n3,2"
             */
            for(Text node : nodes)
            {
                String nodeString = node.toString();
//                logger.info("Reduce: input = " + tipText.toString() + "\t:\t" + nodeString);
                PathNodeInternal ancestor = PathNodeInternal.fromString(nodeString);
                /* accumulate the branch length */
                accumulatedBranchLengths += ancestor.getLength();
                /* not interested in unbranched parents of tips
                 * the higher the ID, the more recent   */
                if ( ancestor.getLabel() > nearestNodeId ) {
                     nearestNodeId = ancestor.getLabel();
                     tipCount = ancestor.getTipCount();
                }
            }
            /*     Write the MRCA: like:
             *     A,C,D        n2,3 or
             *     A,C          n3,2
             *     A            n4,1
             */
            PathNodeInternal mrca = 
                    new PathNodeInternal(nearestNodeId, accumulatedBranchLengths, tipCount);
            context.write(tipText, new Text(mrca.toString()));
//            logger.info("Reduce: output = " + tipText.toString() + "\t:\t" + mrca.toString());
        }
    }
    
}
