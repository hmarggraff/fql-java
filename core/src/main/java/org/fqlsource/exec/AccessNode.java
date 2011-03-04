package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class AccessNode extends FqlNode
{
    private final String member;

    public AccessNode(String member, int row, int col)
    {
        super(row, col);
        this.member = member;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return env.getValue(member, from, env.interatorEntryPoint());
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "member");
    }


}
