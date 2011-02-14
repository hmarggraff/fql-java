package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.FqlDataException;

/**
 */
public class IndexOpNode extends BinaryNode
{
    public IndexOpNode(FqlNodeInterface left, FqlNodeInterface right)
    {
        super(left, right);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        throw new NotYetImplementedError();
    }
}
