package org.phylotastic.SourcePackages.mrppath;
/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

/**
 * PathNodeInternal class
 * The PathNodeInternal class extends the
 * PathNode class with the interger TipCount
 * This count expresses how many tip nodes
 * have the particular node in their path.
 * 
 */
public class PathNodeInternal extends PathNode
{   int mTipCount;

    /**
     * Constructor
     *
     * @param label
     * @param length
     */
    public PathNodeInternal(int label, double length)
    {
        super(label, length);
    }

    /**
     * Constructor
     *
     * @param label
     * @param length
     * @param tipCount
     */
    public PathNodeInternal(int label, double length, int tipCount)
    {
        super(label, length);
        mTipCount = tipCount;
    }

    /**
     * Static method to create an internal pathnode from
     * it's string representation
     *
     * @param node
     * @return
     */
    public static PathNodeInternal parseNode(String node) {
	String[] p = node.split(",");           // extracts the tipcount
	String[] t = p[0].split(":");           // extracts label and length
	return new PathNodeInternal(Integer.parseInt(t[0]),Double.parseDouble(t[1]),Integer.parseInt(p[1]));
    }

    /**
     * Static method to create an internal pathnode from
     * org.apache.hadoop.io.Text representation
     *
     * @param node
     * @return
     */
    static PathNodeInternal parseNode(org.apache.hadoop.io.Text node)
    {
        return PathNodeInternal.parseNode(node.toString());
    }

    /**
     * Set this node's tipcount
     *
     * @param tipCount
     */
    public void setTipCount(int tipCount)
    {
        mTipCount = tipCount;
    }

    /**
     * Get this node's tipcount
     *
     * @return
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