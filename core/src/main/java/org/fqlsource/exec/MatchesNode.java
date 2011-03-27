package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.RunEnv;

import java.util.regex.Pattern;

/**
 */
public class MatchesNode extends BinaryNode
{
    public MatchesNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(left, right, row, col);
    }

    /**
     *
     * @param env The run time environment object
     * @param from  The parent data object
     * @return a Boolean: true if the pattern matchges the string using javas standard regexp logic
     * @throws FqlDataException
     */

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object leftValue = left.getValue(env, from);
        Object rightValue = right.getValue(env, from);
        if (leftValue == null)
        {
            return rightValue != null; //null < everything
        }
        else if (leftValue instanceof String && rightValue instanceof String)
        {
            String lv = (String) leftValue;
            String rv = (String) rightValue;
            return Pattern.matches(lv, rv);

        }
        throw fqlDataException("Comparing classes " + leftValue.getClass() + " with " + rightValue.getClass());
    }
}
