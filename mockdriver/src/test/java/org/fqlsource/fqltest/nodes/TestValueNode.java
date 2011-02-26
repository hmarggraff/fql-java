package org.fqlsource.fqltest.nodes;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.exec.FqlNode;
import org.fqlsource.exec.RunEnv;

public class TestValueNode  extends FqlNode
{
    private final Object value;

    protected TestValueNode(Object value)
    {
        super(1,1);
        this.value = value;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return value;
    }
}
