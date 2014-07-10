package org.phylotastic.SourcePackages;
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

    static InternalTreeNode parseNode(String node)
    {
//		logger.info("node " + node);
        String[] p = node.split(":");
//        logger.info("String p " + Arrays.toString(p));rffr
//		String[] t = p[0].split(":");

        return new InternalTreeNode(Integer.parseInt(p[0]),Double.parseDouble(p[1]));
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

    public String toString ()
    {
        return super.toString() + "," + mTipCount;
    }

}
