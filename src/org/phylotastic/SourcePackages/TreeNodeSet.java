package org.phylotastic.SourcePackages;
/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

import java.util.*;
import org.apache.log4j.Logger;

/**
 *TreeNodeSet class
 * ----description----
 */
public class TreeNodeSet {
    private Set<TreeNode> mTipSet = new HashSet<TreeNode>();
    static Logger logger = Logger.getLogger("org.phylotastic.SourcePackages.TreeNodeSet");

    public static TreeNodeSet parseTreeNodeSet (String tipSet) {
        String[] nodes = tipSet.split("\\|");
//        logger.info("nodes " + Arrays.toString(nodes));
        TreeNodeSet result = new TreeNodeSet();
//        logger.info("results " + result);
        for ( int i = 0; i < nodes.length; i++ ) {
            result.addTip(TreeNode.parseNode(nodes[i]));
        }
//        logger.info("result na vullen " + result);
        return result;
    }

    void addTip(TreeNode node) {
        mTipSet.add(node);
    }

    public int getSize() {
        return mTipSet.size();
    }

    public Set<TreeNode> getTipSet() {
        return mTipSet;
    }

    public String toString() {
        List<TreeNode> tips = new ArrayList<TreeNode>();
        tips.addAll(mTipSet);
        Collections.sort(tips);
        StringBuffer result = new StringBuffer();
        int i = 0;
        for ( TreeNode tip : tips ) {
            i++;
            result.append(tip.toString());
            if ( i < tips.size() ) {
                result.append('|');
            }
        }
        return result.toString();
    }
}
