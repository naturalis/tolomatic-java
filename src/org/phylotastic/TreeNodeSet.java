package org.phylotastic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//-----------------------------------------------------------------------------------------------------------------
// Class doet
public class TreeNodeSet {
	private Set<TreeNode> mTipSet = new HashSet<TreeNode>();
	
	public static TreeNodeSet parseTreeNodeSet (String tipSet) {
		String[] nodes = tipSet.split("\\|");
		TreeNodeSet result = new TreeNodeSet();
		for ( int i = 0; i < nodes.length; i++ ) {
			result.addTip(TreeNode.parseNode(nodes[i]));
		}
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
