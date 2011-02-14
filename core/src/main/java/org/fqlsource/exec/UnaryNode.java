package org.fqlsource.exec;

/**
 */
public abstract class UnaryNode extends FqlNode
{
    FqlNodeInterface right;

    protected UnaryNode(FqlNodeInterface right)
    {
        super();
        this.right = right;
    }

}
