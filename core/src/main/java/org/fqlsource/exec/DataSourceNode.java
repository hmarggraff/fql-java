package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.util.NamedIndex;

/**
 */
public class DataSourceNode extends FqlNode
{
    NamedIndex it;

    public DataSourceNode(NamedIndex it, int row, int col)
    {
        super(row, col);
        this.it = it;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        if (it == null)
        {
            throw new NotYetImplementedError();
        }
        return it;
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "it");
    }

}
