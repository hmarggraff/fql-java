package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;

public class WhereClause implements FqlStatement {
    private final FqlNodeInterface expr;

    public WhereClause(FqlNodeInterface expr) {
        this.expr = expr;
    }

    public FqlIterator execute(final RunEnv env, final FqlIterator precedent) throws FqlDataException {
        return new FqlIterator() {
            public Object next() throws FqlDataException {
                Object t;
                while ((t = precedent.next()) != FqlIterator.sentinel) {
                    Object val = expr.getValue(env, t);
                    if (val != null)
                        if (val instanceof Boolean) {
                            Boolean cond = (Boolean) val;
                            if (cond)
                                return t;
                        } else
                            throw new FqlDataException(String.format("Where condition at %d, " +
                                    "%d does not return a Boolean, but a %s", expr.getRow(), expr.getCol(),
                                    t.getClass().getSimpleName()));
                }
                return FqlIterator.sentinel;
            }
        };
    }
}
