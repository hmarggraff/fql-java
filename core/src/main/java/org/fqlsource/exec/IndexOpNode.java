package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlMapContainer;
import org.fqlsource.data.RunEnv;

/**
 */
public class IndexOpNode extends BinaryNode
{
    public IndexOpNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(left, right, row, col);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        FqlMapContainer mapContainer = (FqlMapContainer) left.getValue(env, from);
        Object key = right.getValue(env, from);
        Object ret = mapContainer.lookup(env, key);
        return ret;
    }
}
