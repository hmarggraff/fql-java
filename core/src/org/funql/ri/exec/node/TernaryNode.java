package org.funql.ri.exec.node;

/**
 */
public abstract class TernaryNode extends BinaryNode
{
    FqlNodeInterface head;

    protected TernaryNode(FqlNodeInterface head, FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(left, right, row, col);
        this.head = head;
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "head", "left", "right");
    }

}
