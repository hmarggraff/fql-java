package org.fqlsource.exec;

/**
 */
public abstract class TernaryNode extends BinaryNode
{
    FqlNodeInterface head;

    protected TernaryNode(FqlNodeInterface head, FqlNodeInterface left, FqlNodeInterface right)
    {
        super(left, right);
        this.head = head;
    }
}
