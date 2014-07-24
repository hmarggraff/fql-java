package org.funql.ri.exec.clause;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.NamedValues;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.exec.node.FqlNodeInterface;

/**
 * User: hmf
 * Date: 21.05.13
 * Time: 21:49
 * To change this template use File | Settings | File Templates.
 */
public class LimitClause implements FqlStatement {
    private FqlNodeInterface node;

    public LimitClause(FqlNodeInterface node) {
        this.node = node;
    }

    @Override
    public FqlIterator execute(final RunEnv env, final FqlIterator precedent) throws FqlDataException {
        Object value = node.getValue(env, null);
        if (!(value instanceof Long))
            throw new FqlDataException("Limit is not a number, but a: " + value.getClass().getName());
        final long limit = (long) value;
        return new FqlIterator() {
            int at = 0;
            @Override
            public NamedValues next() {
                if (at >= limit)
                    return FqlIterator.sentinel;
                at++;
                return precedent.next();
            }
        };
    }
}
