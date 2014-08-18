/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

package org.phylotastic.SourcePackages.mrptree;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Tree {
    private TreeNode root;
    private final Map<Integer, TreeNode> nodes;

    // constructor
    // ------------------------------------------------------------------------
    /** Construct a Tree object
    */
    public Tree() {
        super();
        root = null;
        nodes = new HashMap<>();
    }
    
    public void addNode(TreeNode _node) {
        nodes.put(_node.getID(), _node);
    }
    
    public TreeNode addNode(int _nodeID, String _name, double _length) {
        TreeNode node = new TreeNode(_nodeID, _name, _length);
        this.addNode(node);
        return node;
    }
    
    public Boolean hasNode(int _nodeID) {
        return nodes.containsKey(_nodeID);
    }
    
    public TreeNode getNode(int _nodeID) {
        return nodes.get(_nodeID);
    }
    
    public void setChild(TreeNode _parent, TreeNode _child) {
        _parent.addChild(_child);
    }
    
    private TreeNode findTheRoot() {
        int lowestID = (int)999999999;
        for (int id : nodes.keySet()) {
            if (id < lowestID)
                lowestID = id;
        }
        return nodes.get(lowestID);
    }
    
    private void rootTheNodes(TreeNode _root) {
        for (TreeNode node : nodes.values()) {
            if (!node.hasParent() && !node.isRootNode())
                this.root.addChild(node);
        }
    }
    
    public void rootTheTree() {
        this.root = this.findTheRoot();
        this.root.setAsRootNode(true);
        this.rootTheNodes(this.root);
    }
    
    public TreeNode getRoot() {
        return this.root;
    }
    
    public String toNewick() {
        StringBuilder newickString = new StringBuilder();
        this.root.toNewick(newickString);
        return newickString.toString();
    }
    
    private void walkTheTree(TreeNode node, List<TreeNode> list) {
        list.add(node);
        for (TreeNode child : node.getChildren())
            walkTheTree(child, list);
    }
        
    /**
     * Methods andsuch
     * tree.toString()
     * tree.toNewick()
     */
    
}
