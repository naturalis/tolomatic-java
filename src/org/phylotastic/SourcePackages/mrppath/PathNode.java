package org.phylotastic.SourcePackages.mrppath;
/**
 * Author(s); Rutger Vos, Carla Stegehuis
 * Contributed to:
 * Date:
 * Version: 0.1
 */

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;
import org.apache.log4j.Logger;

/**
 * PathNode class
 * The Pathnode class is a helper class for the 
 * map/reduce/pruner program.
 * Pathnode objects help to store and convert
 * nodes in the paths from the taxcons to their
 * roots.
 * 
 */
public class PathNode implements Comparable<PathNode>, WritableComparable<PathNode> {
    // this
    int mLabel;
    double mLength;
    String mName;
    
    // static
    static Logger logger;

    /**
     * Constructor
     *
     * @param label
     * @param length
     */
    public PathNode(int label,double length) {
        mLabel = label;
        mLength = length;
        mName = "";
    }

    /**
     * Constructor
     *
     * @param label
     * @param length
     * @param name
     */
    public PathNode(int label,double length, String name) {
        mLabel = label;
        mLength = length;
        mName = name;
    }

    /**
     * Static method to create a pathnode from
     * it's string representation
     *
     * @param node
     * @return
     */
    public static PathNode parseNode(String node) {
        String[] parts = node.split(":");
        if (parts.length == (int)2)
            return new PathNode(Integer.parseInt(parts[0]),Double.parseDouble(parts[1]));
        else if (parts.length == (int)3)
            return new PathNode(Integer.parseInt(parts[0]),Double.parseDouble(parts[1]), parts[2]);
            else
                return null;
    }

    /**
     * Returns the string representation
     * of this pathnode
     */
    @Override
    public String toString () {
        if (mName.isEmpty())
            return mLabel + ":" + mLength;
        else
            return mLabel + ":" + mLength + ":" + mName;
    }

    /**
     * set this node's label (ID)
     *
     * @param label
     */
    public void setLabel (int label) {
        mLabel = label;
    }

    /**
     * Get this node's label
     *
     * @return
     */
    public int getLabel () {
        return mLabel;
    }

    /**
     * Set this node's length
     * i.e. the length between it and its parent's node
     *
     * @param length
     */
    public void setLength (double length) {
        mLength = length;
    }

    /**
     * Get this node's legth
     *
     * @return
     */
    public double getLength () {
        return mLength;
    }

    /**
     * Set this (external) node's name
     *
     * @param name
     */
    public void setName (String name) {
        mName = name;
    }

    /**
     * Return this (external) node's name
     *
     * @return
     */
    public String getName () {
        return mName;
    }

    /**
     * compareTo implements (part of) the Comparable<> interface
     * compareTo implements (part of) the WritableComparable interface
     * 
     * returns:
     *          -1 if that node's label is < this node's label
     *           0 if that node's label is = this node's label
     *          +1 if that node's label is > this node's label
     * 
     * @param that
     * @return 
     */
    @Override
    public int compareTo(PathNode that) {
        int thisValue = this.getLabel();
        int thatValue = that.getLabel();
        return (thatValue < thisValue ? -1 : (thatValue > thisValue ? 1 : 0));
    }

    /**
     * readFields implements (part of) the WritableComparable interface
     * 
     * @param in
     * @throws java.io.IOException
     */
    @Override
    public void readFields(DataInput in) throws IOException {
        this.setLabel(in.readInt());
        char semiColon = in.readChar();
        this.setLength(in.readDouble());
    }

    /**
     * write implements (part of) the WritableComparable interface
     * 
     * @param out
     * @throws java.io.IOException
     */
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.getLabel());
        out.writeChar(':');
        out.writeDouble(this.getLength());
    }

    @Override
    public boolean equals(Object other) {
        if ( other instanceof PathNode ) {
            return getLabel() == ((PathNode)other).getLabel();
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.mLabel;
        return hash;
    }
}