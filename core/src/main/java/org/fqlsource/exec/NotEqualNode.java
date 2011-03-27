package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.RunEnv;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 */
public class NotEqualNode extends BinaryNode
{
    public NotEqualNode(FqlNodeInterface left, FqlNodeInterface right, int row, int col)
    {
        super(left, right, row, col);
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return !realEqual(env,from);
    }
}
