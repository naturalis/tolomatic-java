package org.phylotastic.SourcePackages.mrptree;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class TreeNode {
    private int ID;                                     // the node ID
    private String name;                                // the taxon name
    private double length;                              // the length of the node's branch
    private TreeNode parent;                            // the node's parent node
    private List<TreeNode> children;                    // the node's child nodes
    private boolean isRoot;                             // indication if the node is a root node
    
    private static DecimalFormat lengthFormatter;       // a locale insensitive formatter for the length
                                                        // used in the toNewick method

    // constructors
    // ------------------------------------------------------------------------
    /** Create a Tree node object
    */
    public TreeNode() {
        super();
        this.ID = (int) 0;
        this.name = "";
        this.length = (double) 0.0;
        this.parent = null;
        this.children = new ArrayList<>();
        this.isRoot = false;
        this.setNumberFormat();
    }
    
    /**
     * Create a TreeNode object
     *
     * @param _id
     * @param _length
     */
    public TreeNode(Integer _id, Double _length) {
        super();
        this.ID = _id;
        this.name = "";
        this.length = _length;
        this.parent = null;
        this.children = new ArrayList<>();
        this.isRoot = false;
        this.setNumberFormat();
    }
    
    /**
     * Create a TreeNode object
     *
     * @param _id
     * @param _length
     * @param _name
     */
    public TreeNode(Integer _id, Double _length, String _name) {
        super();
        this.ID = _id;
        this.length = _length;
        this.name = _name;
        this.parent = null;
        this.children = new ArrayList<>();
        this.isRoot = false;
        this.setNumberFormat();
    }
    
    /**
     * Create a Number format so that toNewick outputs
     * a period as the decimal separator for length in
     * spite of the current "locale".
     * eg in NL the separator would be a comma, which would
     * conflict with the use of the comma in newick as the
     * node separator.
     */
    private void setNumberFormat() {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "US"));
        lengthFormatter = (DecimalFormat)nf;
        lengthFormatter.applyPattern("##0.0#");
    }
    
    /**
     * Set this node's ID (= label)
     * 
     * @param _id
     */
    public void setID(int _id) {
        this.ID = _id;
    }
    
    /**
     * Return this node's ID
     * 
     * @return
     */
    public int getID() {
        return this.ID;
    }
    
    /**
     * Set this node's name
     *
     * @param _name
     */
    public void setName(String _name) {
        this.name = _name;
        if (this.name.isEmpty())
            this.name = String.valueOf(this.ID);
    }
    
    /**
     * Return this node's name
     *
     * @return
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Return this node's name; create a name
     * from the node ID if the name is empty.
     * Diferentiates between internal and
     * external nodes
     *
     * @return
     */
    public String getNodeName() {
        if (this.name.isEmpty())
            if (this.hasChildren())
                return "i" + String.valueOf(this.ID);
            else
                return "e" + String.valueOf(this.ID);
        else
            return this.name;
    }
    
    /**
     * Set the length of the distance between
     * this (child) node and it's parent node
     *
     * @param _length
     */
    public void setLength(Double _length) {
        this.length = _length;
    }
    
    /**
     * Return the length of the distance between
     * this (child) node and it's parent node
     *
     * @return
     */
    public Double getLength() {
        return this.length;
    }
    
    /**
     * Set this node as a child of another node (parent)
     *
     * @param _parentNode
     */
    public void setParent(TreeNode _parentNode) {
        this.parent = _parentNode;
    }
    
    /**
     * Return the parent node of a given child node
     *
     * @param _childNode
     * @return
     */
    public TreeNode getParent(TreeNode _childNode) {
        return this.parent;
    }
    
    /**
     * Return true if this node has a parent
     *
     * @return
     */
    public Boolean hasParent() {
        return (this.parent != null);
    }
    
    /**
     * Add a given node as a child to this node
     *
     * @param _childNode
     */
    public void addChild(TreeNode _childNode) {
        this.children.add(_childNode);
        _childNode.parent = this;
    }
    
    /**
     * Return true if this node has children
     *
     * @return
     */
    public boolean hasChildren() {
        return (!this.children.isEmpty());
    }
    
    /**
     * Return a List of this node's children
     *
     * @return
     */
    public List<TreeNode> getChildren() {
        if (this.children == null) {
            return new ArrayList<>();
        }
        return this.children;
    }
    
    /**
     * Set the isRoot property to true or false
     *
     * @param _value
     */
    public void setAsRootNode(boolean _value) {
        this.isRoot = _value;
    }
    
    /**
     * Return true if this node is 
     * designated as the root node
     *
     * @return
     */    
    public Boolean isRootNode() {
        return this.isRoot;
    }
    
    /**
     * Return a string representation of this node
     *
     * @return
     */ 
    @Override
    public String toString() {
        return String.format("%d,%s:%.1f", this.ID, this.name, this.length);
    }
    
    /**
     * Return a newick representation of this node
     * and this node's children and those node's children and ...
     * I.e. this method is recursive.
     *
     * @param newickString
     */ 
    public void toNewick(StringBuilder newickString) {
        if (this.hasChildren()) {
            // it is an internal node that will have children of it's own
            // it's representation includes that of it's children.
            // open a child group for this node (the "(" character)
            newickString.append("(");
            String separator = "";
            // process this node's children
            for (TreeNode child : this.children) {
                newickString.append(separator);
                child.toNewick(newickString);
                if (!",".equals(separator)) separator = ",";
            }
            // close the child group for this node (the ")" character)
            newickString.append(")");
            // add this node's data (name and length)
            newickString.append(String.format("%s:%s", this.getNodeName(), lengthFormatter.format(this.length)));
            if (this.isRoot)
                // then it is the end of the proces
                // the newick string should now be complete
                // close the newick string with a semicolon ";"
                newickString.append(";");
        }
        else
            // it is an external (tip) node that does not have any children of it's own
            newickString.append(String.format("%s:%s", this.getNodeName(), lengthFormatter.format(this.length)));
    }
}
