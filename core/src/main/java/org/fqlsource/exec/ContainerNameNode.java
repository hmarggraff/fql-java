package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlMapContainer;
import org.fqlsource.data.RunEnv;
import org.fqlsource.util.NamedIndex;

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
        FqlMapContainer mapContainer = env.getMapContainer(it.index);
        return mapContainer;
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, it.getName());
    }


}
