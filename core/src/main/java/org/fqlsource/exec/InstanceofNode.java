package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.RunEnv;

import java.util.Date;

/**
 * Node for the <i>Is</i> operator
 */
public class InstanceofNode extends UnaryNode
{
    private final String targetType;

    public InstanceofNode(FqlNodeInterface left, String targetType, int row, int col)
    {
        super(left, row, col);
        this.targetType = targetType;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object v = right.getValue(env, from);
        if ("String".equals(targetType))
        {
            return v instanceof String;
        }
        else if ("Number".equals(targetType))
        {
            return v instanceof Number;
        }
        else if ("Date".equals(targetType))
        {
            return v instanceof Date;
        }
        else if ("Float".equals(targetType))
        {
            return v instanceof Float || v instanceof Double;
        }
        else if ("Int".equals(targetType))
        {
            return v instanceof Byte || v instanceof Short || v instanceof Integer || v instanceof Long;
        }
        final Class c = v.getClass();
        if ("Object".equals(targetType))
        {
            return !c.isPrimitive() && !c.isArray();
        }
        else if ("Array".equals(targetType))
        {
            return c.isArray() || v instanceof Iterable;
        }
        else
        {
            return targetType.equals(c.getName());
        }
    }

    public void dump(StringBuffer sb)
    {
        lispify(sb, "targetType", "right");
    }


}
