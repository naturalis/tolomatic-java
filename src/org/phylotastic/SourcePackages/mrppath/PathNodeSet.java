package org.phylotastic.SourcePackages.mrppath;

import java.util.TreeSet;

public class PathNodeSet {
    private TreeSet<PathNode> mTipSet;
    /**
    * This class maintains a set of PathNodes in a TreeSet 
    * and contains some helper methods for it.
    * 
    * A java.util.TreeSet is used instead of a HashSet, because TreeSet 
    * implements the SortedSet<> interface; each pathNode added is inserted 
    * in it's correct place in the following order, according to it's (int)
    * label. In order for this to work PathNode must implement the Comparable
    * interface and so have an implemented compareTo() method.
    */

    /**
     * Constructor
     * Creates an empty PathNode set
     */
    public PathNodeSet() {
        super();
        mTipSet = new TreeSet<>();
    }

    /**
     * Static method to create a filled pathnodeset from
     * it's string representation
     *
     * @param nodeList
     * @return
     */
    public static PathNodeSet parsePathNodeSet (String nodeList) {
        String[] nodes = nodeList.split("\\|");
        PathNodeSet result = new PathNodeSet();
        for (String node : nodes) {
            result.addNode(PathNode.parseNode(node));
        }
        return result;
    }

    /**
     * Add a node to this set
     *
     * @param node
     */
    public void addNode(PathNode node) {
        mTipSet.add(node);
    }

    /**
     * Return the number of nodes in this set
     *
     * @return
     */
    public int getSize() {
        return mTipSet.size();
    }

    /**
     * Return the (adress of the) TreeSet that 
     * contains the pathnodeset
     *
     * @return
     */
    public TreeSet<PathNode> getSet() {
        return mTipSet;    
    }

    /**
     * Return a string representation
     * of the pathnodeset in whitch
     * the nodes are seperated by 
     * the "|" character
     * 
     * @return 
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String separator = "";
        for ( PathNode node : mTipSet ) {
            result.append(separator);
            result.append(node.toString());
            if ( separator.isEmpty() )
                separator = "|";
        }
        return result.toString();
    }    
}
