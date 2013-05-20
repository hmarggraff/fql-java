package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

/**
 */
public class DotNode extends UnaryNode
{
    protected String memberName;
    EntryPointSlot entryPointSlot; // the key in the array of init connections

    public DotNode(FqlNodeInterface left, String memberName, EntryPointSlot entryPointSlot, int row, int col)
    {
        super(left, row, col);
        this.memberName = memberName;
        this.entryPointSlot = entryPointSlot;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftVal = operand.getValue(env, from);
        return env.getValue(memberName, leftVal, entryPointSlot.getIndex());
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "memberName", "right");
    }

}
