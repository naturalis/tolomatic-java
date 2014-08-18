/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */
package org.phylotastic.SourcePackages.mrptree;

import java.util.List;
import java.util.ArrayList;

public class TreeNode {
    private int ID;
    private String name;
    private double length;
    private TreeNode parent;
    private List<TreeNode> children;
    private boolean isRoot;

    // constructors
    // ------------------------------------------------------------------------
    /** Construct a Tree object
    */
    public TreeNode() {
        super();
        this.ID = (int) 0;
        this.name = "";
        this.length = (double) 0.0;
        this.parent = null;
        this.children = new ArrayList<>();
        this.isRoot = false;
    }
    
    public TreeNode(Integer _id, Double _length) {
        super();
        this.ID = _id;
        this.name = "";
        this.length = _length;
        this.parent = null;
        this.children = new ArrayList<>();
        this.isRoot = false;
    }
    
    public TreeNode(Integer _id, String _name, Double _length) {
        super();
        this.ID = _id;
        this.name = _name;
        this.length = _length;
        this.parent = null;
        this.children = new ArrayList<>();
        this.isRoot = false;
    }
    
    public void setID(int _id) {
        this.ID = _id;
    }
    
    public int getID() {
        return this.ID;
    }
    
    public void setName(String _name) {
        this.name = _name;
        if (this.name.isEmpty())
            this.name = String.valueOf(this.ID);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getNodeName() {
        if (this.name.isEmpty())
            if (this.hasChildren())
                return "i" + String.valueOf(this.ID);
            else
                return "e" + String.valueOf(this.ID);
        else
            return this.name;
    }
    
    public void setLength(Double _length) {
        this.length = _length;
    }
    
    public Double getLength() {
        return this.length;
    }
    
    public void setParent(TreeNode _parentNode) {
        this.parent = _parentNode;
    }
    
    public TreeNode getParent(TreeNode _childNode) {
        return this.parent;
    }
    
    public Boolean hasParent() {
        return (this.parent != null);
    }
    
    public void addChild(TreeNode _childNode) {
        this.children.add(_childNode);
        _childNode.parent = this;
    }
    
    public boolean hasChildren() {
        return (!this.children.isEmpty());
    }
    
    public List<TreeNode> getChildren() {
        if (this.children == null) {
            return new ArrayList<>();
        }
        return this.children;
    }
    
    public void setAsRootNode(boolean _value) {
        this.isRoot = _value;
    }
    
    public Boolean isRootNode() {
        return this.isRoot;
    }
    
    @Override
    public String toString() {
        return String.format("%d,%s:%.1f", this.ID, this.name, this.length);
    }
    
    public void toNewick(StringBuilder newickString) {
        if (this.hasChildren()) {
            newickString.append("(");
            String separator = "";
            for (TreeNode child : this.children) {
                newickString.append(separator);
                child.toNewick(newickString);
                if (!",".equals(separator)) separator = ",";
            }
            newickString.append(")");
            if (this.isRoot)
                newickString.append(this.getNodeName()).append(";");
            else
                newickString.append(String.format("%s:%.1f", this.getNodeName(), this.length));
        }
        else
            newickString.append(String.format("%s:%.1f", this.getNodeName(), this.length));
    //System.out.println(newickString);
    }
}
