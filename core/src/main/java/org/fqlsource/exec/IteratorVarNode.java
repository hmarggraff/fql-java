package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.parser.IteratorVar;

/**
 */
public class IteratorVarNode extends FqlNode
{
    IteratorVar it;

    public IteratorVarNode(IteratorVar it, int row, int col)
    {
        super(row, col);
        this.it = it;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        if (it == null)
            throw new NotYetImplementedError();
        return it;
    }
}
