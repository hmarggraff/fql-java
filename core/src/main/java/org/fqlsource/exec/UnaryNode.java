package org.fqlsource.exec;

/**
 */
public abstract class UnaryNode extends FqlNode
{
    FqlNodeInterface right;

    protected UnaryNode(FqlNodeInterface right, int row, int col)
    {
        super(row, col);
        this.right = right;
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "right");
    }


}
