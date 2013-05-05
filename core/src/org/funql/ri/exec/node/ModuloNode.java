package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.exec.RunEnv;

/**
 */
public class ModuloNode extends BinaryNode
{
    public ModuloNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(left, right, row, col);
    }

    /**
     * @param env  The run time environment object
     * @param from The parent data object
     * @return a Boolean: true if the pattern matchges the string using javas standard regexp logic
     * @throws org.funql.ri.data.FqlDataException
     *
     */

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftValue = left.getValue(env, from);
        Object rightValue = operand.getValue(env, from);
        if (leftValue == null || rightValue == null)
            return null;
        else if ((leftValue instanceof Long || leftValue instanceof Integer || leftValue instanceof Byte || leftValue instanceof Short)
              && (rightValue instanceof Long || rightValue instanceof Integer || rightValue instanceof Byte || rightValue instanceof Short))
        {
            try
            {
                long rNum = ((Number) rightValue).longValue();
                return ((Number) leftValue).longValue() % rNum;
            }
            catch (Throwable ex) // Division by Zero
            {
                throw new FqlDataException(ex, row, col);
            }
        }
        throw fqlDataException("Cannot apply modulo operator to classes " + leftValue.getClass() + " and " + rightValue.getClass());
    }
}
