package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.util.NamedIndex;

/**
 */
public class ContainerNameNode extends FqlNode
{
    NamedIndex containerIndex;

    public ContainerNameNode(NamedIndex containerIndex, int row, int col)
    {
        super(row, col);
        this.containerIndex = containerIndex;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return env.getMapContainer(containerIndex.index);
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, containerIndex.getName());
    }

    public NamedIndex getContainerIndex()
    {
        return containerIndex;
    }
}
