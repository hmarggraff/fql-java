package org.funql.ri.exec.node;

/**
 */
public abstract class UnaryNode extends FqlNode
{
    FqlNodeInterface operand;

    protected UnaryNode(FqlNodeInterface operand, int row, int col)
    {
        super(row, col);
        this.operand = operand;
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "right");
    }

    @Override
    public void buildMemberName(StringBuffer target) {
    }

    public FqlNodeInterface getOperand() {
        return operand;
    }

    @Override
    public boolean visit(NodeVisitor visitor)
    {
        final boolean stop = operand.visit(visitor);
        if (stop) return stop;
        return visitor.process(this);
    }
}
