package org.fqlsource.fqltest.nodes;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.exec.node.FqlNode;

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
