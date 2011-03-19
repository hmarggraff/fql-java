package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.util.NamedIndex;

/**
 */
public class AccessNode extends FqlNode
{
    private final NamedIndex source;
    private final String memberName;

    public AccessNode(NamedIndex source, String memberName, int row, int col)
    {
        super(row, col);
        this.source = source;
        this.memberName = memberName;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return env.getValue(memberName, from, source.getIndex());
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "memberName");
    }


    public String getMemberName()
    {
        return memberName;
    }
}
