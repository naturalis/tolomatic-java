package org.phylotastic.SourcePackages.mapreducepruner;


import org.phylotastic.SourcePackages.mrppath.*;
import org.phylotastic.SourcePackages.mrpconfig.MrpConfig;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import org.apache.log4j.Logger;


public class MrpPass3 {
    private static MrpConfig configP3;
    private static final Text IDtext = new Text("(=)");
    //
    private static Logger loggerP3;
    private static Logger debugLoggerP3;
    
    /**
     *
     * @param _config
     */
    public static void setEnviron(MrpConfig _config) {
        configP3 = _config;
        loggerP3 = Logger.getLogger(MrpPass3.class.getName());
        debugLoggerP3 = Logger.getLogger("debugLogger");
    }

    // class: Map
    // ------------------------------------------------------------------------
    /**
     * Map class, as element of the MapReducePruner class
     */
    public static class Pass3Map extends Mapper<Text, Text, Text, Text> 
    {
        /**
         * Map-3 step prepares the data for the names to be re-attaches to
         * their respective tip nodes and for any remainig internal nodes that
         * subtend only 1 tip, to be removed.
         *
         * For example, for tree
         * 
         *         (n1)				  A        C   D
         *         /  \				   \      /   /
         *       (n2)  \			   (n4)  /   /
         *       /  \   \			     \  /   /
         *     (n3)  \   \			     (n3)  /
         *     /  \   \   \			       \  /
         *   (n4)  \   \   \                           (n2)
         *   /  \   \   \   \
         *  A    B   C   D   E
         *
         * if taxons are A and C and D, Map-3() receives the records:
         * (=)		A:Agoracea
         * (=)		C:Catonacea
         * (=)		D:Draconacea
         * A		n4,1
         * A,C		n3,2
         * A,C,D	n2,3
         *
         * in no perticular following order
         *
         * processing will:
         * - split the tipsets into ID/node pairs
         * - split the name-item into an ID/name pair
         *
         * after processing Map-3() emits the records:
         * labelOf(A)	A:Agoracea
         * labelOf(C)	C:Catonacea
         * labelOf(D)	D:Draconacea
         * labelOf(A)	n2,3
         * labelOf(C)	n2,3
         * labelOf(D)	n2,3
         * labelOf(A)	n3,3
         * labelOf(C)	n3,3
         * labelOf(A)	n4,1
         * 
         * instead of the tipNode.toString() value, now the integer value of tipNode.getLabel()
         * is used as the key-part of the map output. node.toString() is the value-part.
         * This is not a necessity for the process, but the label's integer value prefixed
         * with zeroes to 6 positions (String.format("%06d", node.getLabel())) should give 
         * a cleaner sort result and therefore an easier to (manually) check output.
         * It might also be of help for further processing of the result into a tree.
         *
         * Before being presented to the Reduce-3() method, the emited records are sorted, giving
         * A	n2,3
         * A	n3,2
         * A	n4,1
         * A	=A:Agoracea
         * C	n2,3
         * C	n3,2
         * C	=C:Catonacea
         * D	n2,3
         * D	=D:Draconacea
         * 
         * where it says A, C and D the integer labels of those nodes are meant.
         * like in the case of the parkia:
         * "000628       623:1"
         * "000628       625:1"
         * "000628       =628:18:parkia"
         * 
         * @param tipText
         * @param nodeText
         * @param context
         * @throws java.io.IOException
         * @throws java.lang.InterruptedException
         */
        
        @Override
        public void map(Text tipText, Text nodeText, Context context) throws IOException, InterruptedException
        {
            String tipString = tipText.toString();
            String nodeString = nodeText.toString();
            if (tipText.equals(IDtext)) {
                // it is a tip name record like:
                // "(=)		628:18:parkia"
                // write out exchanging IDtext with the tip's label
                // "000628      628:18:parkia"
                debugLoggerP3.debug("Map: input = " + "(=)" + "\t:\t" + nodeString);
                PathNode tipNode = PathNode.parseNode(nodeString);
                // get a string representation of the label,
                // prefixed with zeroes to 6 positions
                String tipLabel = String.format("%06d", tipNode.getLabel());
                // write the new name record
                context.write(new Text(tipLabel), new Text("=" + nodeString));
                debugLoggerP3.debug("Map: output = " + tipLabel + "\t:\t" + nodeString);
            } else {
                // it is a tip set like:
                // A,C,D	n2,3
                // write out as seperate tip/node pairs; e.g.
                // labelOf(A)	n2,3
                // labelOf(C)	n2,3
                // labelOf(D)	n2,3
                // where only the tip's 6 position label represents the tip
                // like in the case of the parkia:
                // "000628       623:1"
                // "000628       625:1"
                debugLoggerP3.debug("Map: input = " + tipString + "\t:\t" + nodeString);
                // split line into tipSet and ancestor
                PathNodeSet tipSet = PathNodeSet.parsePathNodeSet(tipString);
                // for each tip write the record.
                for ( PathNode tipNode : tipSet.getSet() ) {
                    String tipLabel = String.format("%06d", tipNode.getLabel());
                    context.write(new Text(tipLabel), nodeText);
                    debugLoggerP3.debug("Map: output = " + tipLabel + "\t:\t" + nodeString);
                }
            }
        }
    }

    // class: reduce
    // ------------------------------------------------------------------------
    /**
     * Reduce class, as element of the MapReducePruner class
     */
    //
    public static class Pass3Reduce extends Reducer<Text, Text, Text, Text>
    {
        /**
         * For example, for tree
         * 
         *         (n1)				  A        C   D
         *         /  \				   \      /   /
         *       (n2)  \			   (n4)  /   /
         *       /  \   \			     \  /   /
         *     (n3)  \   \			     (n3)  /
         *     /  \   \   \			       \  /
         *   (n4)  \   \   \            	       (n2)
         *   /  \   \   \   \
         *  A    B   C   D   E
         *
         * if taxons are A and C and D, Reduce-3() receives:
         * A	{n2,3;n3,2;n4,1;=A:Agoracea}    // {} = iterable list
         * C	{n2,3;n3,2;=C:Catonacea} 	// {} = iterable list
         * D	{n2,3;=D:Draconacea}    	// {} = iterable list
         *
         * where it lists the keys A, C and D the integer labels of those nodes are meant.
         * 
         * processing will:
         * - assemble the path "per tip"
         * - remove internal nodes that only subtend 1 tip
         *   adding their branch length to that of the tip
         *
         * after processing Reduce-3() emits the records:
         * A:Agoracea		n2,n3
         * C:Catonacea		n2,n3
         * D:Draconacea		n2
         *
         * Representing the tree
         * 
         *      (n2)				A        C   D
         *      /  \				 \      /   /
         *    (n3)  D:Draconacea		  \    /   /	
         *    /  \				   \  /   /
         *   /    C:Catonacea                      (n3)  /
         *  /					     \  /
         * A:Agoracea           		     (n2)
         *
	 * further processing can then result in the Newick representation for the tree:
         * 
         * @param tipText
         * @param nodes
         * @param context
         * @throws java.io.IOException
         * @throws java.lang.InterruptedException
         */

        @Override
        public void reduce(Text tipText, Iterable<Text> nodes, Context context) throws IOException, InterruptedException
        {
            String tipString = tipText.toString();
            // this is a taxon node set like this:
            // A	n2,3; n3,2; n4,1 and =A:Agoracea
            // where A is the external node's label tipText
            // and n2,n3,n4, and =A:Agoracea are the taxon's path nodes
            // like in the case of the parkia:
            // "000628      623:1, 625:1 and =628:18:parkia"
            PathNode taxonTip = null;
            PathNodeInternal taxonNode = null;
            PathNodeSet taxonPath = new PathNodeSet();
            int taxonLength = 0;
            for(Text node : nodes)
            {
                String nodeString = node.toString();
                debugLoggerP3.debug("reduce: input = " + tipString + "\t:\t" + nodeString);
                if (nodeString.startsWith("=")) {
                    // it is the name node
                    taxonTip = PathNode.parseNode(nodeString.substring(1));
                    taxonLength += taxonTip.getLength();
                } else {
                    // it is an internal node; neglect those only subtending
                    // one tip; add their length to that of the tip node
                    taxonNode = PathNodeInternal.parseNode(nodeString);
                    int tipCount = taxonNode.getTipCount();
                    if (tipCount == (int)1)
                        taxonLength += tipCount;
                    else
                        taxonPath.addNode(new PathNode(taxonNode.getLabel(), taxonNode.getLength()));
                }
            }
            // adjust the taxons branch length
            taxonTip.setLength(taxonLength);
            // write the result: e.g.
            // "628:18:parkia   ..., 622:1, 623:1, ..."
            context.write(new Text(taxonTip.toString()), new Text(taxonPath.toString()));
            debugLoggerP3.debug("Reduce: output = " + taxonTip.toString() + "\t:\t" + taxonPath.toString());
        }
    }
}
