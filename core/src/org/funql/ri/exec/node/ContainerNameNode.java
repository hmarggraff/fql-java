package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
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
        FqlMapContainer mapContainer = env.getMap(it.index);
        return mapContainer;
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, it.getName());
    }


}
