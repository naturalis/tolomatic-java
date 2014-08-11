package org.phylotastic.SourcePackages;
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
 * TreeNode class
 * ----description----
 */
public class TreeNode implements WritableComparable<TreeNode> {
    int mLabel;
    double mLength;
    static Logger logger = Logger.getLogger("org.phylotastic.SourcePackages.TreeNode");


    public TreeNode(int label,double length) {
        mLabel = label;
        mLength = length;
    }

    static TreeNode parseNode(String node) {
//        logger.info("node " + node);
        String[] parts = node.split(":");
//        logger.info("parts 0 " + Integer.parseInt(parts[0]));
//        logger.info("parts 1 " + Double.parseDouble(parts[1]));
        return new TreeNode(Integer.parseInt(parts[0]),Double.parseDouble(parts[1]));
    }

    @Override
    public String toString () {
        return mLabel + ":" + mLength;
    }

    public void setLabel (int label) {
        mLabel = label;
    }

    public int getLabel () {
        return mLabel;
    }

    public void setLength (double length) {
        mLength = length;
    }

    public double getLength () {
        return mLength;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.setLabel(in.readInt());
        char semiColon = in.readChar();
        this.setLength(in.readDouble());
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.getLabel());
        out.writeChar(':');
        out.writeDouble(this.getLength());
    }

    @Override
    public int compareTo(TreeNode other) {
        int thisValue = this.getLabel();
        int thatValue = other.getLabel();
        return (thisValue > thatValue ? -1 : (thisValue==thatValue ? 0 : 1));

    }

    @Override
    public boolean equals(Object other) {
        if ( other instanceof TreeNode ) {
            return getLabel() == ((TreeNode)other).getLabel();
        }
        else {
            return false;
        }
    }

}