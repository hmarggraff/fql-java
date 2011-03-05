package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class DotNode extends UnaryNode
{
    protected String memberName;
    int entryPointIndex; // the key in the array of open connections

    public DotNode(FqlNodeInterface left, String memberName, int entryPointIndex, int row, int col)
    {
        super(left, row, col);
        this.memberName = memberName;
        this.entryPointIndex = entryPointIndex;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftVal = right.getValue(env, from);
        return env.getValue(memberName, leftVal, entryPointIndex);
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "memberName", "right");
    }

}
