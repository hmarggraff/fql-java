package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class DotNode extends UnaryNode
{
    protected String memberName;

    public DotNode(FqlNodeInterface left, String memberName)
    {
        super(left, row, col);
        this.memberName = memberName;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftVal = right.getValue(env, from);
        return env.getValue(memberName, leftVal);
    }
}
