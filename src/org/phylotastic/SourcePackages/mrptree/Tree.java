package org.phylotastic.SourcePackages.mrptree;

import java.util.Map;
import java.util.HashMap;

import org.phylotastic.SourcePackages.mrppath.*;

public class Tree {
    /**
     * Constructs a tree that can be used to construct
     * a newick tree from
     */
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
    
    /**
     * add a TreeNode
     * 
     * @param _node
     */
    public void addNode(TreeNode _node) {
        nodes.put(_node.getID(), _node);
    }
    
    /**
     * add a TreeNode
     *
     * @param _nodeID
     * @param _length
     * @param _name
     * @return
     */
    public TreeNode addNode(int _nodeID, double _length, String _name) {
        TreeNode node = new TreeNode(_nodeID, _length, _name);
        this.addNode(node);
        return node;
    }
    
    /**
     * add a TreeNode from a (MrpPath) PathNode
     *
     * @param _node
     * @return
     */
    public TreeNode addNode(PathNode _node) {
        TreeNode node = new TreeNode(_node.getLabel(), _node.getLength(), _node.getName());
        this.addNode(node);
        return node;
    }
    
    /**
     * add a TreeNode from an internal (MrpPath) PathNode
     *
     * @param _node
     * @return
     */
    public TreeNode addNode(PathNodeInternal _node) {
        TreeNode node = new TreeNode(_node.getLabel(), _node.getLength());
        this.addNode(node);
        return node;
    }
    
    /**
     * Check if the tree contains a node
     * with a specific key (label/ID)
     *
     * @param _nodeID
     * @return
     */
    public Boolean hasNode(int _nodeID) {
        return nodes.containsKey(_nodeID);
    }
    
    /**
     * Return the node with a
     * specific key (label/ID)
     *
     * @param _nodeID
     * @return
     */
    public TreeNode getNode(int _nodeID) {
        return nodes.get(_nodeID);
    }
    
    /**
     * Add a node as a child to another treenode (the parent)
     *
     * @param _parent
     * @param _child
     */
    public void setChild(TreeNode _parent, TreeNode _child) {
        _parent.addChild(_child);
    }
    
    /**
     * Find the node with the lowest ID (label
     *
     * @return
     */
    private TreeNode findTheRoot() {
        int lowestID = (int)999999999;
        for (int id : nodes.keySet()) {
            if (id < lowestID)
                lowestID = id;
        }
        return nodes.get(lowestID);
    }
    
    /**
     * Connect all the nodes that do not yet have
     * a parent to a specified node as their parent node
     *
     * @param _root
     */
    private void rootTheNodes(TreeNode _root) {
        for (TreeNode node : nodes.values()) {
            if (!node.hasParent() && !node.isRootNode())
                this.root.addChild(node);
        }
    }
    
    /**
     * Make an unrooted tree to a rooted tree
     * by designating the (first) node with the
     * lowest ID as the root node.
     */
    public void rootTheTree() {
        this.root = this.findTheRoot();
        this.root.setAsRootNode(true);
        this.rootTheNodes(this.root);
    }
    
    /**
     * return the root node of the tree
     *
     * @return
     */
    public TreeNode getRoot() {
        return this.root;
    }
    
    /**
     * return a newick representation of the tree
     *
     * @return
     */
    public String toNewick() {
        StringBuilder newickString = new StringBuilder();
        this.root.toNewick(newickString);
        return newickString.toString();
    }
}
