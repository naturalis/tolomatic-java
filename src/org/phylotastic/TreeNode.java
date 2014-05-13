package org.phylotastic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

//-----------------------------------------------------------------------------------------------------------------
// Class doet
public class TreeNode implements WritableComparable<TreeNode> {
	int mLabel;
	double mLength;
	
	public TreeNode(int label,double length) {
		mLabel = label;
		mLength = length;
	}
	
	static TreeNode parseNode(String node) {
		String[] parts = node.split(":");
		return new TreeNode(Integer.parseInt(parts[0]),Double.parseDouble(parts[1]));
	}
	
	public String toString () {
		return mLabel + ":" + mLength;
	}
	
	void setLabel (int label) {
		mLabel = label;
	}
	
	int getLabel () {
		return mLabel;
	}
	
	void setLength (double length) {
		mLength = length;
	}
	
	double getLength () {
		return mLength;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		setLabel(in.readInt());
		char semiColon = in.readChar();
		setLength(in.readDouble());
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(getLabel());
		out.writeChar(':');
		out.writeDouble(getLength());
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
