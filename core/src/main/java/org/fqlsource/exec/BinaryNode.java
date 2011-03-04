package org.fqlsource.exec;

/**
 */
public abstract class BinaryNode extends UnaryNode
{
    FqlNodeInterface left;

    protected BinaryNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(right, row, col);
        this.left = left;
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "left", "right");
    }

}
