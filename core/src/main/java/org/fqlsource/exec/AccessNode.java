package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.util.NamedIndex;

/**
 */
public class AccessNode extends FqlNode
{
    private final NamedIndex source;
    private final String member;

    public AccessNode(NamedIndex source, String member, int row, int col)
    {
        super(row, col);
        this.source = source;
        this.member = member;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return env.getValue(member, from, source.getIndex());
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "member");
    }


}
