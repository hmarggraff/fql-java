package org.funql.ri.exec.clause;

import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.NamedValues;
import org.funql.ri.exec.RunEnv;

/**
 * Date: 18.05.13 09:05
 */
public class NestedClauseIterator implements FqlIterator {
    private final FqlIterator precedent;

    public NestedClauseIterator(RunEnv env, FqlIterator precedent) {
        this.precedent = precedent;
    }

    @Override
    public NamedValues next() {
        return precedent.next();
    }
}
