package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlMapContainer;
import org.funql.ri.exec.RunEnv;

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
        Object key = operand.getValue(env, from);
        Object ret = mapContainer.lookup(key);
        return ret;
    }
}
