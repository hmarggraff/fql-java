package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class AccessNode extends FqlNode
{
    private final String member;

    public AccessNode(String member)
    {
        super(row, col);
        this.member = member;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return env.getValue(member, from);
    }

}
