package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;

/**
 * Date: 18.05.13 09:00
 */
public class EndClause implements FqlStatement {

    @Override
    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException {
        return new NestedClauseIterator(env, precedent);
    }
}
