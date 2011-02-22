package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.FqlDataException;

/**
 */
public class CollectionSliceNode extends TernaryNode
{
    public CollectionSliceNode(FqlNodeInterface head, FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(head, left, right, row, col);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        final Object srcCollection = head.getValue(env, from);
        if (srcCollection == null)
            return null;
        final Object start = left.getValue(env, from);
        if (start == null)
            return null;
        if (!(start instanceof Byte || start instanceof Short || start instanceof Integer|| start instanceof Long))
            throw new FqlDataException("Start index of collection slice is not an Integer.", row, col);
        final Object end = right.getValue(env, from);
        if (end == null)
            return null;
        if (!(end instanceof Byte || end instanceof Short || end instanceof Integer|| end instanceof Long))
            throw new FqlDataException("End index of collection slice is not an Integer.", row, col);
        final int startIx = ((Number) start).intValue();
        final int endIx = ((Number) end).intValue();
        if (startIx >= endIx)
            return null;
        return new CollectionSlice(srcCollection, startIx, endIx, row, col);
    }
}
