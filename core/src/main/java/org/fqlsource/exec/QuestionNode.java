package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.FqlDataException;

/**
 */
public class QuestionNode extends TernaryNode
{
    public QuestionNode(FqlNodeInterface head, FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(head, left, right, row, col);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        Object condVal = head.getValue(env, from);
        if (condVal == null)
            return false;
        checkClassOf(condVal, Boolean.class, "Condition", "?");
        final Object val;
        if ((Boolean)condVal)
            val = left.getValue(env, from);
        else
            val = right.getValue(env, from);
        return val;
    }
}
