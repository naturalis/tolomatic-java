package org.phylotastic.mapreducepruner;
//package org.phylotastic.SourcePackages.mapreducepruner;

import org.phylotastic.mrppath.*;
//import org.phylotastic.SourcePackages.mrppath.*;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

/**
 *
 * @author ...
 */
public class MrpPass2 {
    private static final Text IDtext = new Text("(=)");
    //
    // sara 23-09-2014 (changed 2 lines, because of removing setEnviron)
    private static Logger loggerP2 = Logger.getLogger(MrpPass2.class.getName());;
    private static Logger debugLoggerP2 = Logger.getLogger("debugLogger");;
    
    /**
     *  creates logger for reporting purposes
     */
    public static void setEnviron() {
	// sara 23-09-2014 (deleted all lines)
        // loggerP2 = Logger.getLogger(MrpPass2.class.getName());
        // debugLoggerP2 = Logger.getLogger("debugLogger");
    }

    /** class: Pass2Map
     * -------------------------------------------------------------------------
     * Mapper class, an element of the Hadoop MapReduce framework
     */
    public static class Pass2Map extends Mapper<Text, Text, Text, Text>
    {
        /** method: map
         * 
         * For example, for tree
         * 
         *         (n1)                           A        C   D
         *         /  \                            \      /   /
         *       (n2)  \                           (n4)  /   /
         *       /  \   \                            \  /   /
         *     (n3)  \   \                           (n3)  /
         *     /  \   \   \                            \  /
         *   (n4)  \   \   \                           (n2)
         *   /  \   \   \   \                            \
         *  A    B   C   D   E                           (n1)
         *
         * if taxons are A and C and D, Map-2() receives the records:
         * (=)          A:Agoracea
         * (=)          C:Catonacea
         * (=)          D:Draconacea
         * A,C,D        n1,3
         * A,C,D        n2,3
         * A,C          n3,2
         *
         * in no perticular following order
         *
         * Map2 does not do any processing; it is only there
         * to enable the Reduce2 process
         *
         * after processing Map-2() emits the records:
         * (=)          A:Agoracea
         * (=)          C:Catonacea
         * (=)          D:Draconacea
         * A,C,D        n1,3
         * A,C,D        n2,3
         * A,C          n3,2
         * A            n4,1
         *
         * Before being presented to the Reduce-2() method, the emited records are sorted, giving
         * (=)          A:Agoracea
         * (=)          C:Catonacea
         * (=)          D:Draconacea
         * A            n4,1
         * A,C          n3,2
         * A,C,D        n1,3
         * A,C,D        n2,3
         * 
         * input format = key-value pairs !!
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
                // it is a tip name record like:
                // "(=)         A:Agoracea"
                // write out without further processing: e.g.
                // (=)          A:Agoracea
                debugLoggerP2.debug("Map: input = " + "(=)" + "\t:\t" + node.toString());
                context.write(tipSet, node);
                debugLoggerP2.debug("Map: output = " + "(=)" + "\t:\t" + node.toString());
            } else {
                // it is a counted tip set like:
                // A,C,D        n1,3
                // write out without further processing: e.g.
                // A,C,D        n1,3
                String nodeString = node.toString();
                debugLoggerP2.debug("Map: input = " + tipString + "\t:\t" + nodeString);
                context.write(tipSet, node);
                debugLoggerP2.debug("Map: output = " + tipString + "\t:\t" + nodeString);
            }            
        }        
    }

    /** class: Pass2Reduce
     * -------------------------------------------------------------------------
     * Reducer class, an element of the Hadoop MapReduce framework
     */
    public static class Pass2Reduce extends Reducer<Text, Text, Text, Text>
    {
        /** method: reduce
         * 
         * Given a concatenated list of tips, a node ID and the number of tips it subtends,
         * ( like: A|B,n1,2).
         * In cases where a concatenated list of tips has multiple internal nodes 
         * associated with it, this means that there are unbranched internal nodes on 
         * the path from those tips to the root. Of those unbranched internals we want the 
         * MRCA of the tips, which we obtain by sorting the node IDs: since these are 
         * applied in pre-order, the highest node ID in the list is the MRCA.
         * (MRCA = Most recent common ancestor)
         * 
         * For example, for tree
         * 
         *         (n1)                           A        C   D
         *         /  \	                           \      /   /
         *       (n2)  \                           (n4)  /   /
         *       /  \   \                            \  /   /
         *     (n3)  \   \                           (n3)  /
         *     /  \   \   \                            \  /
         *   (n4)  \   \   \                           (n2)
         *   /  \   \   \   \                            \
         *  A    B   C   D   E                           (n1)
         *
         * if taxons are A and C and D, Reduce() receives the records:
         * (=)          {A:Agoracea,C:Catonacea,D:Draconacea}   // {} = iterable list
         * A            {n4,1}                                  // {} = iterable list
         * A,C          {n3,2}                                  // {} = iterable list
         * A,C,D        {n1,3; n2,3}                            // {} = iterable list
         *
         * processing will:
         * - neglect unbranched nodes in multi-node tiplist		=> like n1 for A,C,D
         *   and regard only the MRCA (Most recent common ancestor)	=> like n2 for A,C,D
         * - add the branche length of neglected nodes to
         *   that of their MRCA
         *
         * after processing Reduce-2() emits the records:
         * (=)          A:Agoracea
         * (=)          C:Catonacea
         * (=)          D:Draconacea
         * A            n4,1
         * A,C          n3,2
         * A,C,D        n2,3
         *
         * Representing the tree
         * 
         *       (n2)                            A        C   D
         *       /  \                             \      /   /
         *     (n3)  \                            (n4)  /   /
         *     /  \   \                             \  /   /
         *   (n4)  \   \                            (n3)  /
         *   /      \   \                             \  /
         *  A        C   D                            (n2)
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
            String tipString = tipText.toString();
            if (tipText.equals(IDtext)) {
                // it is a taxon name set like this:
                // "(=)         {A:Agoracea,C:Catonacea,D:Draconacea}
                // where (=) is in tipText
                // and A:Agoracea, C:Catonacea and D:Draconacea are the nodes
                // write out without further processing: e.g.
                // (=)          A:Agoracea
                // (=)          C:Catonacea
                // (=)          D:Draconacea
                for (Text taxonID : nodes) {
                    debugLoggerP2.debug("Reduce: input = " + "(=)" + "\t:\t" + taxonID.toString());
                    context.write(tipText, taxonID);
                    debugLoggerP2.debug("Reduce: output = " + "(=)" + "\t:\t" + taxonID.toString());
                }
            } else {
                // it is a set of counted tip sets like:
                // A,C          {n3,2}
                // A,C,D        {n1,3; n2,3}
                // processing will:
                // neglect unbranched nodes in multi-node tiplist, like n1 for A,C,D
                // regard only the MRCA (Most recent common ancestor), like n2 for A,C,D
                // The MRCA is the one with the highest ID/Label; in this case n2
                // so the example would write:
                // A,C          n3,2
                // A,C,D        n2,3
                // adding the length of neglected unbrached nodes to the
                // length of the MRCA.
                double accumulatedBranchLengths = 0;
                int nearestNodeId = 0;
                int tipCount = 0;
                // find the MRCA whitch is the one with the highest ID (= label)
                // like n2 in: "A,C,D    {n1,3; n2,3}"
                // or the only one there is, like n3 in: "A,C    n3,2"
                for(Text node : nodes)
                {
                    String nodeString = node.toString();
                    debugLoggerP2.debug("Reduce: input = " + tipString + "\t:\t" + nodeString);
                    PathNodeInternal ancestor = PathNodeInternal.parseNode(nodeString);
                    // accumulate the branch length
                    accumulatedBranchLengths += ancestor.getLength();
                    // not interested in unbranched parents of tips
                    // the higher the ID, the more recent
                    if ( ancestor.getLabel() > nearestNodeId ) {
                         nearestNodeId = ancestor.getLabel();
                         tipCount = ancestor.getTipCount();
                    }
                }
                // Write the MRCA: like: 
                // A,C,D        n2,3 or
                // A,C          n3,2
                // A            n4,1
                PathNodeInternal mrca = new PathNodeInternal(nearestNodeId,accumulatedBranchLengths, tipCount);
                context.write(tipText, new Text(mrca.toString()));
                debugLoggerP2.debug("Reduce: output = " + tipString + "\t:\t" + mrca.toString());
            }
        }
    }
}
