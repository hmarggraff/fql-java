package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

/**
 */
public class TypeCastNode extends UnaryNode
{
    private final String targetType;

    public TypeCastNode(FqlNodeInterface left, String targetType, int row, int col)
    {
        super(left, row, col);
        this.targetType = targetType;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return right.getValue(env, from);
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "targetType", "right");
    }

}
