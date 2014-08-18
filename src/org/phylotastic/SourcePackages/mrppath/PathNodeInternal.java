/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */
package org.phylotastic.SourcePackages.mrppath;

import org.apache.log4j.Logger;
//import org.w3c.dom.Text;

/**
 * PathNodeInternal class
 ----description----
 */
public class PathNodeInternal extends PathNode
{
    int mTipCount;
    static Logger logger = Logger.getLogger("org.phylotastic.SourcePackages.InternalTreeNode");

    public PathNodeInternal(int label, double length)
    {
        super(label, length);
    }

    public PathNodeInternal(int label, double length, int tipCount)
    {
        super(label, length);
        mTipCount = tipCount;
    }
	
    public static PathNodeInternal parseNode(String node) {
	String[] p = node.split(",");
	String[] t = p[0].split(":");
	return new PathNodeInternal(Integer.parseInt(t[0]),Double.parseDouble(t[1]),Integer.parseInt(p[1]));
    }

    static PathNodeInternal parseNode(org.apache.hadoop.io.Text node)
    {
        return PathNodeInternal.parseNode(node.toString());
    }

    public int getTipCount()
    {
        return mTipCount;
    }

    public void setTipCount(int tipCount)
    {
        mTipCount = tipCount;
    }

    @Override
    public String toString ()
    {
        return super.toString() + "," + mTipCount;
    }

}