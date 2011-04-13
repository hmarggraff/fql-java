package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.RunEnv;
import org.fqlsource.util.NamedIndex;

/**
 * Allows to use the stream name in an expression.
 * getValue returns the current object of the iterator
 */
public class MemberNode extends FqlNode
{
    private final NamedIndex source;
    private final String memberName;

    public MemberNode(NamedIndex source, String memberName, int row, int col)
    {
        super(row, col);
        this.source = source;
        this.memberName = memberName;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return env.getValueFromIterator(memberName, from);
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "memberName", "source");
    }

    public String getMemberName()
    {
        return memberName;
    }
}
