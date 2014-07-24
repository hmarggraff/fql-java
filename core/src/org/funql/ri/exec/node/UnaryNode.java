package org.funql.ri.exec.node;

import org.funql.ri.exec.NodeVisitor;

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
        final boolean stop = visitor.process(this);
        if (stop) return stop;
        return operand.visit(visitor);
    }
}
