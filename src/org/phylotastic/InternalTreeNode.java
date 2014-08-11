package org.phylotastic;
/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

import org.apache.log4j.Logger;
//import org.w3c.dom.Text;

/**
 * InternalTreeNode class
 * ----description----
 */
public class InternalTreeNode extends TreeNode
{
    int mTipCount;
    static Logger logger = Logger.getLogger("org.phylotastic.SourcePackages.InternalTreeNode");

    public InternalTreeNode(int label, double length)
    {
        super(label, length);
    }

    public InternalTreeNode(int label, double length, int tipCount)
    {
        super(label, length);
        mTipCount = tipCount;
    }
	
    static InternalTreeNode parseNode(String node) {
	String[] p = node.split(",");
	String[] t = p[0].split(":");
	return new InternalTreeNode(Integer.parseInt(t[0]),Double.parseDouble(t[1]),Integer.parseInt(p[1]));
    }

    static InternalTreeNode parseNode(org.apache.hadoop.io.Text node)
    {
        return InternalTreeNode.parseNode(node.toString());
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