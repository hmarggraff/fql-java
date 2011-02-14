package org.fqlsource.exec;

/**
 */
public abstract class BinaryNode extends UnaryNode
{
    FqlNodeInterface left;

    protected BinaryNode(FqlNodeInterface left, FqlNodeInterface right)
    {
        super(right);
        this.left = left;
    }
}
