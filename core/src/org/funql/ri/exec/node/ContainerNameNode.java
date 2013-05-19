package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.FqlMapContainer;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.util.NamedIndex;

/**
 */
public class ContainerNameNode extends FqlNode
{
    NamedIndex it;

    public ContainerNameNode(NamedIndex it, int row, int col)
    {
        super(row, col);
        this.it = it;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        FqlMapContainer container = env.getMapContainer(it.index);
        if (container instanceof FqlIterator)
            ((FqlIterator) container).current();
        return container;
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, it.getName());
    }


}
