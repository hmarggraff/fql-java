package org.funql.ri.exec.clause;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.NamedValues;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.exec.node.FqlNodeInterface;

public class WhereClause implements FqlStatement {
    private final FqlNodeInterface expr;

    public WhereClause(FqlNodeInterface expr) {
        this.expr = expr;
    }

    public FqlIterator execute(final RunEnv env, final FqlIterator precedent) throws FqlDataException {
        return new FqlIterator() {
            public NamedValues next() throws FqlDataException {
                while (true) {
		    NamedValues t = precedent.next();
                    if (t == FqlIterator.sentinel)
                        return t;
                    try {
                        env.pushObject(t);
                        Object val = expr.getValue(env, t);
                        if (val != null) {
                            if (val instanceof Boolean) {
                                if ((Boolean) val)
                                    return t;
                            } else
                                throw new FqlDataException(String.format("Where condition at %d, " +
                                        "%d does not return a Boolean, but a %s", expr.getRow(), expr.getCol(),
                                        t.getClass().getSimpleName()));
                        }
                    } finally {
                        env.popObject();
                    }
                }
            }
        };
    }

    public FqlNodeInterface getExpr() {
        return expr;
    }
}
