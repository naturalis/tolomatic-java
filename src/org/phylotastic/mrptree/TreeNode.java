package org.phylotastic.mrptree;

import java.util.TreeSet;
import java.util.Locale;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 *     Class TreeNode
 * 
 *     Used in combination with the Tree class to Construct
 *     a tree structure that can be used to construct a newick tree from
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class TreeNode implements Comparable<TreeNode> {
    /**
     *     Static variable: lengthFormatter
     * 
     *     A locale insensitive formatter for the (branche) length
     *     used in the toString() and toNewick() methods.
     *     The formatter, based on the EN/US locale is set by
     *     the static method:formatLength().
     */
    private static DecimalFormat lengthFormatter = null;
    
    /**
     *     Static method: formatLength()
     * 
     *     Return a Double formatted such that toNewick outputs
     *     a period as the decimal separator for length in
     *     spite of the current "locale".
     *     e.g. in NL the separator would be a comma, which would
     *     conflict with the use of the comma in newick as the
     *     node separator.
     */
    private static String formatLength(Double _length) {
        if (lengthFormatter == null) {
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "US"));
            lengthFormatter = (DecimalFormat)nf;
            lengthFormatter.applyPattern("##0.000000#");
        }
        return lengthFormatter.format(_length);
    }
    
    /**
     *     Object variables:
     */ 
    private int ID;                                     // the node ID
    private String name;                                // the taxon name
    private double length;                              // the length of the node's branch
    private TreeNode parent;                            // the node's parent node
    private TreeSet<TreeNode> children;                 // the node's child nodes => TreeSet supports sorted set
    private boolean isRoot;                             // indication if the node is a root node

    // constructors
    // ------------------------------------------------------------------------
    /**     Create a Tree node object
    */
    public TreeNode() {
        super();
        this.ID = (int) 0;
        this.name = "";
        this.length = (double) 0.0;
        this.nodeInit();
    }
    
    /**
     *     Create a TreeNode object
     *     mainly used for internal treenodes
     *
     * @param _id           the digital id for the treenode
     * @param _length       the distance to the node's parentnode
     */
    public TreeNode(Integer _id, Double _length) {
        super();
        this.ID = _id;
        this.name = "";
        this.length = _length;
        this.nodeInit();
    }
    
    /**
     *     Create a TreeNode object
     *     mainly used for external treenodes (tips or leafs)
     *
     * @param _id           the digital id for the treenode
     * @param _length       the distance to the node's parentnode
     * @param _name         the name of the taxon this is the external node for
     */
    public TreeNode(Integer _id, Double _length, String _name) {
        super();
        this.ID = _id;
        this.length = _length;
        this.name = _name;
        this.nodeInit();
    }
    
    /**
     *     Set this node's ditital ID (= label)
     * 
     * @param _id           the integer id for the treenode
     */
    private void nodeInit() {
        this.parent = null;
        this.children = new TreeSet<>();
        this.isRoot = false;
    }
    
    /**
     *     Set this node's ditital ID (= label)
     * 
     * @param _id           the integer id for the treenode
     */
    public void setID(int _id) {
        this.ID = _id;
    }
    
    /**
     *     Return this node's digital ID
     * 
     * @return             the integer id for the treenode
     */
    public int getID() {
        return this.ID;
    }
    
    /**
     *     Set this node's name
     *
     * @param _name        the (external) node's taxon name
     */
    public void setName(String _name) {
        this.name = _name;
        if (this.name.isEmpty())
            this.name = String.valueOf(this.ID);
    }
    
    /**
     *     Return this node's name
     *
     * @return        the (external) node's taxon name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     *     Return this node's name; create a name
     *     from the node ID if the name is empty.
     *     Differentiates between internal and
     *     external nodes
     *
     * @return        the node's name
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
     *     Set the length of the distance between
     *     this (child) node and it's parent node
     *
     * @param _length       the distance to the node's parentnode
     */
    public void setLength(Double _length) {
        this.length = _length;
    }
    
    /**
     *     Return the length of the distance between
     *     this (child) node and it's parent node
     *
     * @return       the distance to the node's parentnode
     */
    public Double getLength() {
        return this.length;
    }
    
    /**
     *     Set this node as a child of another node (parent)
     *
     * @param _parentNode       the parentnode for this node
     */
    public void setParent(TreeNode _parentNode) {
        this.parent = _parentNode;
    }
    
    /**
     *     Return the parent node of this.child node
     *
     * @return                  the parent node for this.child node
     */
    public TreeNode getParent() {
        return this.parent;
    }
    
    /**
     *     Return true if this node has a parent
     *
     * @return      true if this node has a parent
     */
    public Boolean hasParent() {
        return (this.parent != null);
    }
    
    /**
     *     Add a given node as a child to this node
     *
     * @param _childNode    the node that is to be added as a child
     */
    public void addChild(TreeNode _childNode) {
        this.children.add(_childNode);
        _childNode.parent = this;
    }
    
    /**
     *     Return true if this node has children
     *
     * @return      true if this node has children
     */
    public boolean hasChildren() {
        return (!this.children.isEmpty());
    }
    
    /**
     *     Set the isRoot property to true or false
     *
     * @param _value        the boolean value for te isRooot property
     */
    public void setAsRootNode(boolean _value) {
        this.isRoot = _value;
    }
    
    /**
     *     Return true if this node is
     *     designated as the root node
     *
     * @return      true if this node is a root node
     */    
    public Boolean isRootNode() {
        return this.isRoot;
    }

    /**
     *     compareTo implements (part of) the Coparable interface
     * 
     *     returns:
     *              -1 if this node's label is less then that node's label
     *               0 if this node's label is equal to that node's label
     *              +1 if this node's label is larger then that node's label
     * 
     * @param that  the TreeNode to compare this.node with
     * @return  -1, 0 or +1 depending on the result
     */
    @Override
    public int compareTo(TreeNode that) {
        int thisValue = this.getID();
        int thatValue = that.getID();
        return (thisValue < thatValue ? -1 : (thisValue > thatValue ? 1 : 0));
    }
    
    /**
     *     Return true if this node is equal to
     *     the other node; i.e. has same values
     *
     * @param _that the object (TreeNode) to compare with
     * @return      true if both nodes are equal
     */   
    @Override
    public boolean equals(Object _that) {
        // If _that has no object assigned, return false
        if (_that == null) return false;
        // If _that is not of the same class, return false
        if (_that.getClass() != this.getClass()) return false;
        TreeNode that = (TreeNode)_that;
        // return whether this is equal to that
        return (that.getID() == this.getID()) &&
                (Double.compare(that.getLength(), this.getLength()) == 0) && 
                (that.getName().equals(this.getName()));
    }
    
    /**
     *     Return this node's hashCode.
     *     (algorithm = auto generated)
     * 
     *     (NB When overriding "Equals" also
     *     hashCode has to be overridden)
     * 
     * @return      the TreeNode's hashcode
     */   
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.ID;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.length) ^ 
                (Double.doubleToLongBits(this.length) >>> 32));
        return hash;
    }
    
    /**
     *     Return a string representation of this node
     *
     * @return      the node as a string
     */ 
    @Override
    public String toString() {
        return String.format("%d:%s:%s", this.ID, this.name, TreeNode.formatLength(this.length));
    }
    
    /**
     *     Return a newick representation of this node
     *     and this node's children and those node's children and ...
     *     I.e. this method is recursive.
     *
     * @param newickString      a stringbuilder that the node's newick "string" will be added to
     */ 
    public void toNewick(StringBuilder newickString) {
        if (this.hasChildren()) {
            // it is an internal node that will have children of it's own
            // it's representation includes that of it's children.
            // open a child group for this node (i.e. the "(" character)
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
            newickString.append(String.format("%s:%s", this.getNodeName(), TreeNode.formatLength(this.length)));
            if (this.isRoot)
                // then it is the end of the proces
                // the newick string should now be complete
                // close the newick string with a semicolon ";"
                newickString.append(";");
        }
        else
            // it is an external (tip) node that does not have any children of it's own
            newickString.append(String.format("%s:%s", this.getNodeName(), TreeNode.formatLength(this.length)));
    }
}
