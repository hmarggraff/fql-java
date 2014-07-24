package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.EntryPointSlot;
import org.funql.ri.exec.RunEnv;

/**
 * Allows to use the stream name in an expression.
 * getValue returns the current object of the iterator
 */
public class MemberNode extends FqlNode
{
    private final String memberName;
    private final EntryPointSlot dataSlot;

    public MemberNode(String memberName, EntryPointSlot dataSlot, int row, int col)
    {
        super(row, col);
        this.memberName = memberName;
	this.dataSlot = dataSlot;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return env.getMember(from, memberName, dataSlot);
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "memberName", "source");
    }

    @Override
    public void buildMemberName(StringBuffer target) {
	target.setLength(0);
        target.append(memberName);
    }

    public String getMemberName()
    {
        return memberName;
    }
}
