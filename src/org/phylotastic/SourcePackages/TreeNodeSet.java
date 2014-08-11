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
    private Set<TreeNode> mTipSet = new HashSet<>();
    static Logger logger = Logger.getLogger("org.phylotastic.SourcePackages.TreeNodeSet");

    public static TreeNodeSet parseTreeNodeSet (String tipSet) {
        String[] nodes = tipSet.split("\\|");
//        logger.info("nodes " + Arrays.toString(nodes));
        TreeNodeSet result = new TreeNodeSet();
        for (String node : nodes) {
            result.addTip(TreeNode.parseNode(node));
        }
//        logger.info("result na vullen " + result);
        return result;
    }

    public void addTip(TreeNode node) {
        mTipSet.add(node);
    }

    public int getSize() {
        return mTipSet.size();
    }

    public Set<TreeNode> getTipSet() {
        return mTipSet;
    }

    @Override
    public String toString() {
        List<TreeNode> tips = new ArrayList<>();
        tips.addAll(mTipSet);
        Collections.sort(tips);
        StringBuilder result = new StringBuilder();
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