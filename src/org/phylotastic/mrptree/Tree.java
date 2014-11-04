package org.phylotastic.mrptree;

import java.util.Map;
import java.util.TreeMap;

import org.phylotastic.mrppath.*;

/**
 *     Class Tree
 * 
 *     Constructs a tree structure that can be used to construct
 *     a newick tree from
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class Tree {
    private TreeNode root;                          // the root node
    private final Map<Integer, TreeNode> nodes;     // list of added nodes

    // constructor
    // ------------------------------------------------------------------------
    /**     Construct a Tree object
    */
    public Tree() {
        super();
        root = null;
        nodes = new TreeMap<>();
    }
    
    /**
     *     add a TreeNode
     * 
     * @param _node     a TreeNode to add to this tree
     */
    public void addNode(TreeNode _node) {
        nodes.put(_node.getID(), _node);
    }
    
    /**
     *     Create a TreeNode and add it to this.tree
     *
     * @param _nodeID       the digital id for the treenode
     * @param _length       the distance to the node's parentnode
     * @param _name         the name of the taxon this is the external node for
     * @return              the created node
     */
    public TreeNode addNode(int _nodeID, double _length, String _name) {
        TreeNode node = new TreeNode(_nodeID, _length, _name);
        this.addNode(node);
        return node;
    }
    
    /**
     *     Create a TreeNode and add it to this.tree
     *
     * @param _nodeID       the digital id for the treenode
     * @param _length       the distance to the node's parentnode
     * @return              the created node
     */
    public TreeNode addNode(int _nodeID, double _length) {
        TreeNode node = new TreeNode(_nodeID, _length, "");
        this.addNode(node);
        return node;
    }
    
    /**
     *     add a TreeNode from a (MrpPath) PathNode
     *
     * @param _node     the (external) PathNode to create a TreeNode of
     * @return          the created node
     */
    public TreeNode addNode(PathNode _node) {
        TreeNode node = new TreeNode(_node.getLabel(), _node.getLength(), _node.getName());
        this.addNode(node);
        return node;
    }
    
    /**
     *     add a TreeNode from an internal (MrpPath) PathNode
     *
     * @param _node     the internal PathNode to create a TreeNode of
     * @return          the created node
     */
    public TreeNode addNode(PathNodeInternal _node) {
        TreeNode node = new TreeNode(_node.getLabel(), _node.getLength());
        this.addNode(node);
        return node;
    }
    
    /**
     *     Check if the tree contains a node
     *     with a specific key (label/ID)
     *
     * @param _nodeID       the nodeID for which a TreeNode is searched
     * @return              true if this.tree contains the requested node
     */
    public Boolean hasNode(int _nodeID) {
        return nodes.containsKey(_nodeID);
    }
    
    /**
     *     Return the node with a
     *     specific key (label/ID)
     *
     * @param _nodeID       the nodeID for which a TreeNode is searched
     * @return              the TreeNode with the requested nodeID
     */
    public TreeNode getNode(int _nodeID) {
        return nodes.get(_nodeID);
    }
    
    /**
     *     Add a node as a child to another treenode (the parent)
     *
     * @param _parent       the TreeNode to add the childnode to
     * @param _child        the TreeNode to add as a child
     */
    public void setChild(TreeNode _parent, TreeNode _child) {
        _parent.addChild(_child);
    }
    
    /**
     *     Find the node with the lowest ID (label
     *
     * @return      the lowest of all the nodeID's in the tree
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
     *     Connect all the nodes that do not yet have
     *     a parent to a specified node as their parent node
     *
     * @param _root     the TreeNode that is to serve as the rootnode
     */
    private void rootTheNodes(TreeNode _root) {
        for (TreeNode node : nodes.values()) {
            if (!node.hasParent() && !node.isRootNode())
                this.root.addChild(node);
        }
    }
    
    /**
     *     Make this unrooted tree to a rooted tree
     *     by designating the (first) node with the
     *     lowest ID as the root node.
     */
    public void rootTheTree() {
        // if the tree already has a root, then remove it
        // apparently the tree has to be rerooted
        if (this.root != null) {
            TreeNode oldRoot = this.getRoot();
            oldRoot.setAsRootNode(false);
        }
        // find the (new) rootnode
        this.root = this.findTheRoot();
        this.root.setAsRootNode(true);
        this.rootTheNodes(this.root);
    }
    
    /**
     *     return the root node of the tree
     *
     * @return      the TreeNode that is the rootnode of this.tree
     */
    public TreeNode getRoot() {
        return this.root;
    }
    
    /**
     *     return a newick representation of the tree
     *
     * @return      the Newickk string for this.tree
     */
    public String toNewick() {
        StringBuilder newickString = new StringBuilder();
        this.root.toNewick(newickString);
        return newickString.toString();
    }
}
