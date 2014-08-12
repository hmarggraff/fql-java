package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.ContainerSlot;
import org.funql.ri.exec.RunEnv;

/**
 */
public class DotNode extends UnaryNode
{
    protected String memberName;
    ContainerSlot containerSlot; // the key in the array of init connections

    public DotNode(FqlNodeInterface left, String memberName, ContainerSlot containerSlot, int row, int col)
    {
        super(left, row, col);
        this.memberName = memberName;
        this.containerSlot = containerSlot;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftVal = operand.getValue(env, from);
        return env.getValue(memberName, leftVal, containerSlot.getIndex());
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "memberName", "right");
    }

    public String getMemberName() {
        return memberName;
    }

    public ContainerSlot getContainerSlot() {
        return containerSlot;
    }

    @Override
    public void buildMemberName(StringBuffer target) {
        operand.buildMemberName(target);
        target.append('_');
        target.append(memberName);
    }
}
