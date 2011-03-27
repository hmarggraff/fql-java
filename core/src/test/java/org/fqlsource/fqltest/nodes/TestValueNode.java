package org.fqlsource.fqltest.nodes;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.RunEnv;
import org.fqlsource.exec.FqlNode;

public class TestValueNode extends FqlNode
{
    final Object value;

    protected TestValueNode(Object value)
    {
        super(1, 1);
        this.value = value;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return value;
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "value");
    }
}
