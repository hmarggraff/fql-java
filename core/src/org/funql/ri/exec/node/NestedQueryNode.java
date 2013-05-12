package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;

import java.util.List;

/**
 * Date: 12.05.13 14:33
 */
public class NestedQueryNode extends FqlNode {
    private List<FqlStatement> clauses;

    public NestedQueryNode(List<FqlStatement> clauses, int row, int col) {
        super(row, col);
        this.clauses = clauses;
    }

    @Override
    public Object getValue(RunEnv env, Object from) throws FqlDataException {
        FqlIterator precedent = null;
        for (FqlStatement statement : clauses) {
            precedent = statement.execute(env, precedent);
        }
        return precedent;
    }

    @Override
    public void dump(StringBuffer sb) {
          sb.append(" from ... end ");
    }
}
