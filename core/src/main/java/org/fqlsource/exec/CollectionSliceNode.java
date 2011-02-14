package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.FqlDataException;

/**
 */
public class CollectionSliceNode extends TernaryNode
{
    public CollectionSliceNode(FqlNodeInterface head, FqlNodeInterface left, FqlNodeInterface right)
    {
        super(head, left, right);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        throw new NotYetImplementedError(); //TODO CollectionSliceNode.getValue
    }
}
