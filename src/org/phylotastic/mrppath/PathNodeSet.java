package org.phylotastic.mrppath;
//package org.phylotastic.SourcePackages.mrppath;

import java.util.TreeSet;

/**
 *
 * @author ...
 */
public class PathNodeSet {
    /**
    * This class maintains a set of PathNodes in a (jave.util.)TreeSet 
    * and contains some helper methods for it.
    * 
    * A java.util.TreeSet is used instead of a HashSet, because TreeSet 
    * implements the SortedSet<> interface; each pathNode added is inserted 
    * in it's correct place in the following order, according to it's (int)
    * label. In order for this to work PathNode must implement the Comparable
    * interface and so have an implemented compareTo() method.
    */
    
    /**
     * Static variables:
     */ 
    // none
    
    /**
     * Static method: parsePathNodeSet
     *        
     * to create a filled PathNodeSet from a string representation, like:
     * 628:18|625:1|624:1|623:1|622:1|621:1|581:1|513:1|505:1| ..... |5:1|4:1|3:1|2:1|1:1
     *
     * @param listString    the string version of the node list
     * @return              the created PathNodeSet
     */
    public static PathNodeSet parsePathNodeSet (String listString) {
        String[] nodes = listString.split("\\|");
        PathNodeSet result = new PathNodeSet();
        for (String node : nodes) {
            result.addNode(PathNode.parseNode(node));
        }
        return result;
    }
    
    /**
     * Object variables:
     */ 
    private TreeSet<PathNode> mTipSet;

    /**
     * Constructor
     * Creates an empty PathNode set
     */
    public PathNodeSet() {
        super();
        mTipSet = new TreeSet<>();
    }

    /**
     * Add a node to this set
     *
     * @param node      the PathNode to add to this.PathNodeSet
     */
    public void addNode(PathNode node) {
        mTipSet.add(node);
    }

    /**
     * Return the number of nodes in this set
     *
     * @return   the number of nodes in this.set
     */
    public int getSize() {
        return mTipSet.size();
    }

    /**
     * Return the (reference to) this.java.util.TreeSet
     *
     * @return  the reference to the TreeSet that implements this PathNodeSet
     */
    public TreeSet<PathNode> getSet() {
        return mTipSet;    
    }

    /**
     * Return a string representation
     * of this.pathnodeset in whitch
     * the nodes are seperated by 
     * the "|" character
     * 
     * @return      the PathNodeSet as a string
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
