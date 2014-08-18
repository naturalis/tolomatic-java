/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */
package org.phylotastic.SourcePackages.mrppath;

import java.util.*;
import org.apache.log4j.Logger;

/**
 *PathNodeSet class
 ----description----
 */
public class PathNodeSet {
    private Set<PathNode> mTipSet = new HashSet<>();
    static Logger logger = Logger.getLogger("org.phylotastic.SourcePackages.TreeNodeSet");

    public static PathNodeSet parseTreeNodeSet (String tipSet) {
        String[] nodes = tipSet.split("\\|");
//        logger.info("nodes " + Arrays.toString(nodes));
        PathNodeSet result = new PathNodeSet();
        for (String node : nodes) {
            result.addTip(PathNode.parseNode(node));
        }
//        logger.info("result na vullen " + result);
        return result;
    }

    public void addTip(PathNode node) {
        mTipSet.add(node);
    }

    public int getSize() {
        return mTipSet.size();
    }

    public Set<PathNode> getTipSet() {
        return mTipSet;
    }

    @Override
    public String toString() {
        List<PathNode> tips = new ArrayList<>();
        tips.addAll(mTipSet);
        Collections.sort(tips);
        StringBuilder result = new StringBuilder();
        int i = 0;
        for ( PathNode tip : tips ) {
            i++;
            result.append(tip.toString());
            if ( i < tips.size() ) {
                result.append('|');
            }
        }
        return result.toString();
    }
}