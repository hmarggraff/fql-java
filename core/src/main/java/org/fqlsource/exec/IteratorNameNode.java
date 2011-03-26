package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.util.NamedIndex;

/**
 * Allows to use the stream name in an expression.
 * getValue returns the current object of the iterator
 */
public class IteratorNameNode extends FqlNode
{
    NamedIndex it;

    public IteratorNameNode(NamedIndex it, int row, int col)
    {
        super(row, col);
        this.it = it;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        if (it == null)
        {
            throw new NotYetImplementedError();
        }
        return from;
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, it.getName());
    }

}
