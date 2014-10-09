package org.phylotastic.mrppath;
//package org.phylotastic.SourcePackages.mrppath;

/**
 * PathNodeInternal class
 * The PathNodeInternal class extends the
 * PathNode class with the interger TipCount
 * This count expresses how many (external) tip nodes
 * have the particular (internal) node in their path.
 * 
 * @author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */
public class PathNodeInternal extends PathNode {
    /**
     * Static variables:
     */ 
    // none
    
    /**
     * Static method: parseNode()
     * 
     * To create a pathnode from it's string representation
     *
     * @param nodeString    the string representation of the node, like: 625:1,3
     * @return              the PathNodeInternal created from the string
     */
    public static PathNodeInternal parseNode(String nodeString) {
	String[] p = nodeString.split(",");     // extracts the tipcount
        if (p.length == (int)2) {
            String[] t = p[0].split(":");       // extracts label and length
            if (t.length == (int)2)
                return new PathNodeInternal(Integer.parseInt(t[0]),Double.parseDouble(t[1]),Integer.parseInt(p[1]));
            else
                return null;
        } else
            return null;
    }

    /**
     * Static method: parseNode()
     * 
     * To create an internal pathnode from an
     * org.apache.hadoop.io.Text representation
     *
     * @param nodeText      the Text representation of the node, like: 625:1,3
     * @return              the PathNodeInternal created from the string
     */
    static PathNodeInternal parseNode(org.apache.hadoop.io.Text nodeText)
    {
        return PathNodeInternal.parseNode(nodeText.toString());
    }
    
    /**
     * Object variables:
     */ 
    int mTipCount;

    /**
     * Constructor
     *
     * @param label           the digital id for the pathnode
     * @param length          the distance to the node's parentnode
     */
    public PathNodeInternal(int label, double length)
    {
        super(label, length);
    }

    /**
     * Constructor
     *
     * @param label           the digital id for the pathnode
     * @param length          the distance to the node's parentnode
     * @param tipCount        the number of tip nodes this.PathNode is the external node for
     */
    public PathNodeInternal(int label, double length, int tipCount)
    {
        super(label, length);
        mTipCount = tipCount;
    }

    /**
     * Set this node's tipcount
     *
     * @param tipCount        the number of tip nodes this.PathNode is the external node for
     */
    public void setTipCount(int tipCount)
    {
        mTipCount = tipCount;
    }

    /**
     * Get this node's tipcount
     *
     * @return        the number of tip nodes this.PathNode is the external node for
     */
    public int getTipCount()
    {
        return mTipCount;
    }

    @Override
    public String toString ()
    {
        return super.toString() + "," + mTipCount;
    }

}