package org.phylotastic.mrppath;

import java.util.TreeSet;

/**
 *     Class PathNodeSet
 * 
 *     This class maintains a set of PathNodes in a (jave.util.)TreeSet
 *     and contains some helper methods for it.
 * 
 *     A java.util.TreeSet is used instead of a HashSet, because TreeSet
 *     implements the SortedSet<> interface; each pathNode added is inserted
 *     in it's correct place in the following order, according to it's (int)
 *     label. In order for this to work PathNode must implement the Comparable
 *     interface and so have an implemented compareTo() method.
 * 
 *     PathNodeSets however need to be sorted in descending order and
 *     therefore PathNode implements a "wrong-way-around" compareTo()
 *     method.
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class PathNodeSet {
    /**
     *     Static variables:
     */ 
    // none
    
    /**
     *     Static method: fromString
     *        
     *     to create a filled PathNodeSet from a string representation, like:
     *     628:18|625:1|624:1|623:1|622:1|621:1|581:1|513:1|505:1| ..... |5:1|4:1|3:1|2:1|1:1
     *
     * @param listString    the string version of the node list
     * @return              the created PathNodeSet
     */
    public static PathNodeSet fromString (String listString) {
        String[] nodes = listString.split("\\|");
        PathNodeSet result = new PathNodeSet();
        for (String node : nodes) {
            result.addNode(PathNode.fromString(node));
        }
        return result;
    }
    
    /**
     *     Object variables:
     */ 
    protected TreeSet<PathNode> mTipSet;

    /**
     *     Constructor
     *     Creates an empty PathNode set
     */
    public PathNodeSet() {
        super();
        mTipSet = new TreeSet<>();
    }

    /**
     *     Add a node to this set
     *
     * @param node      the PathNode to add to this.PathNodeSet
     */
    public void addNode(PathNode node) {
        mTipSet.add(node);
    }

    /**
     *     Return the number of nodes in this set
     *
     * @return   the number of nodes in this.set
     */
    public int getSize() {
        return mTipSet.size();
    }

    /**
     *     Return the (reference to) this.java.util.TreeSet
     *
     * @return  the reference to the TreeSet that implements this PathNodeSet
     */
    public TreeSet<PathNode> getSet() {
        return mTipSet;    
    }

    /**
     *     Return a string representation
     *     of this.pathnodeset in whitch
     *     the nodes are seperated by
     *     the "|" character
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
