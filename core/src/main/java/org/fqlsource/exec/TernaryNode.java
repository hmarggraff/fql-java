package org.fqlsource.exec;

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
}
