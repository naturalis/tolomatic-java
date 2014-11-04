package org.phylotastic.mrppath;

/**
 * Class PathNodeInternal
 * 
 * The PathNodeInternal class extends the
 * PathNode class with the interger TipCount
 * This count expresses how many (external) tip nodes
 * have the particular (internal) node in their path.
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class PathNodeInternal extends PathNode {
    /**
     *     Static variables:
     */ 
    // none
    
    /**
     *     Static method: fromString()
     * 
     *     To create a pathnode from it's string representation
     *
     * @param nodeString    the string representation of the node, like: 625:0.1,3
     * @return              the PathNodeInternal created from the string
     */
    public static PathNodeInternal fromString(String nodeString) {
	String[] p = nodeString.split(",");     // extracts the tipcount
        if (p.length == (int)2) {
            String[] t = p[0].split(":");       // extracts label and length
            if (t.length == (int)2)
                return new PathNodeInternal(Integer.parseInt(t[0]),
                        Double.parseDouble(t[1]),Integer.parseInt(p[1]));
            else
                return null;
        } else
            return null;
    }

    /**
     *     Static method: fromText()
     * 
     *     To create an internal pathnode from an
     *     org.apache.hadoop.io.Text representation
     *
     * @param nodeText      the Text representation of the node, like: 625:1.0,3
     * @return              the PathNodeInternal created from the string
     */
    static PathNodeInternal fromText(org.apache.hadoop.io.Text nodeText)
    {
        return PathNodeInternal.fromString(nodeText.toString());
    }
    
    /**
     * Object variables:
     */ 
    int mTipCount;

    /**
     *     Constructor
     *
     * @param label           the digital id for the pathnode
     * @param length          the distance to the node's parentnode
     */
    public PathNodeInternal(int label, double length)
    {
        super(label, length);
    }

    /**
     *     Constructor
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
     *     Constructor
     *
     * @param node            the pathnode containing the label and the length
     * @param tipCount        the number of tip nodes this.PathNode is the external node for
     */
    public PathNodeInternal(PathNode node, int tipCount)
    {
        super(node.getLabel(), node.getLength());
        mTipCount = tipCount;
    }

    /**
     *     Set this node's tipcount
     *
     * @param tipCount        the number of tip nodes this.PathNode is the external node for
     */
    public void setTipCount(int tipCount)
    {
        mTipCount = tipCount;
    }

    /**
     *     Get this node's tipcount
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