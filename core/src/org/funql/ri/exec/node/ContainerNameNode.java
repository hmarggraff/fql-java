package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.FqlMapContainer;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.util.NamedIndex;
import org.funql.ri.util.NotYetImplementedError;

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
        return env.getMapContainer(it.index);
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, it.getName());
    }


}
