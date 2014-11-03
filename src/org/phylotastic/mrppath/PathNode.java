package org.phylotastic.mrppath;

import java.util.Objects;

/**
 *     Class: PathNode
 * 
 *     The Pathnode class is a helper class for the map/reduce/pruner program.
 *     Pathnode objects help to store and convert nodes in the paths from the
 *     taxons to their root nodes.
 *
 *     @author(s); Carla Stegehuis, Rutger Vos
 *     Contributed to:
 *     Date: 3/11/'14
 *     Version: V2.0
 */
public class PathNode implements Comparable<PathNode> {
    /**
     *     Static variables:
     */
    // none
    
    /**
     *     Static method: fromString()
     * 
     *     To create a pathnode from it's string representation
     *
     * @param node      the string representation of the node, like: 625:1 or 628:18:parkia
     * @return          the PathNode created from the string
     */
    public static PathNode fromString(String node) {
        String[] parts = node.split(":");
        if (parts.length == (int)2)
            return new PathNode(Integer.parseInt(parts[0]),Double.parseDouble(parts[1]));
        else if (parts.length == (int)3)
            return new PathNode(Integer.parseInt(parts[0]),Double.parseDouble(parts[1]), parts[2]);
            else
                return null;
    }
    
    /**
     *     Object variables:
     */ 
    int mLabel;
    double mLength;
    String mName;

    /**
     *     Constructor
     *
     * @param _string         a string representation of the pathnode
     */
    public PathNode(String _string) {
        String[] parts = _string.split(":");
        if (parts.length == (int)2) {
            // only label and length are given
            mLabel  = Integer.parseInt(parts[0]);
            mLength = Double.parseDouble(parts[1]);
            mName   = "";
        } else if (parts.length == (int)3) {
            // also name is given
            mLabel  = Integer.parseInt(parts[0]);
            mLength = Double.parseDouble(parts[1]);
            mName   = parts[2];
        } else {
            // invalid number of fields is given
            mLabel = (int)0;
            mLength = (Double)0.0;
            mName = "";
        }
    }

    /**
     *     Constructor
     *
     * @param label           the digital id for the pathnode
     * @param length          the distance to the node's parentnode
     */
    public PathNode(int label, double length) {
        mLabel = label;
        mLength = length;
        mName = "";
    }

    /**
     *     Constructor
     *
     * @param label           the digital id for the pathnode
     * @param length          the distance to the node's parentnode
     * @param name            the name of the taxon this is the external node for
     */
    public PathNode(int label,double length, String name) {
        mLabel = label;
        mLength = length;
        mName = name;
    }

    /**
     *     set this node's label (ID)
     *
     * @param label           the digital id for the pathnode
     */
    public void setLabel (int label) {
        mLabel = label;
    }

    /**
     *     Get this node's label
     *
     * @return           the digital id for the pathnode
     */
    public int getLabel () {
        return mLabel;
    }

    /**
     *     Set this node's length
     *     i.e. the length between it and its parent's node
     *
     * @param length          the distance to the node's parentnode
     */
    public void setLength (double length) {
        mLength = length;
    }

    /**
     *     Get this node's length
     *     i.e. the length between it and its parent's node
     *
     * @return          the distance to the node's parentnode
     */
    public double getLength () {
        return mLength;
    }

    /**
     *     Set this (external) node's name
     *
     * @param name            the name of the taxon this.node is the external node for
     */
    public void setName (String name) {
        mName = name;
    }

    /**
     *     Return this (external) node's name
     *
     * @return            the name of the taxon this.node is the external node for
     */
    public String getName () {
        return mName;
    }

    /**
     *     Returns the string representation of this pathnode
     * 
     * @return      this.PathNode as a string, like: 625:1 or 628:18:parkia
     */
    @Override
    public String toString () {
        if (mName.isEmpty())
            return mLabel + ":" + mLength;
        else
            return mLabel + ":" + mLength + ":" + mName;
    }

    /**
     *     compareTo implements (part of) the Coparable interface
     * 
     *     Beware!
     *     This compareTo is implemented "the wrong way around".
     *     The result is that PathNodeSets will be sorted in
     *     descending order instead of ascending order.
     * 
     *     returns:
     *              -1 if this node's label is larger then that node's label
     *               0 if this node's label is equal to that node's label
     *              +1 if this node's label is less then that node's label
     * 
     * @param that  the PathNode to compare this.node with
     * @return  -1, 0 or +1 depending on the result
     */
    @Override
    public int compareTo(PathNode that) {
        int thisValue = this.getLabel();
        int thatValue = that.getLabel();
        return (thisValue > thatValue ? -1 : (thisValue < thatValue ? 1 : 0));
    }
    
    /**
     *     Return true if this node is equal to
     *     the other node (i.e. has the same ID)
     *
     * @param _that the object (PathNode) to compare with
     * @return      true if both nodes are equal
     */   
    @Override
    public boolean equals(Object _that) {
        // If _that has no object assigned, return false
        if (_that == null) return false;
        // If _that is not of the same class, return false
        if (_that.getClass() != this.getClass()) return false;
        PathNode that = (PathNode)_that;
        // return whether this is equal to that
        return (that.getLabel() == this.getLabel()) &&
                (Double.compare(that.getLength(), this.getLength()) == 0) && 
                (that.getName().equals(this.getName()));
    }
    
    /**
     *     Return this node's hashCode.
     *     (algorithm = auto generated)
     * 
     *     (When overriding "Equals" also
     *     hashCode has to be overridden)
     * 
     * @return      the PathNode's hashcode
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.mLabel;
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.mLength) ^ 
                (Double.doubleToLongBits(this.mLength) >>> 32));
        hash = 11 * hash + Objects.hashCode(this.mName);
        return hash;
    }
}