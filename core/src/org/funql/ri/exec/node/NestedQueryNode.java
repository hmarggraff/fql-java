package org.funql.ri.exec.node;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.util.FqlIterator4Iterable;
import org.funql.ri.util.ListFqlIterator;

import java.util.ArrayList;
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
        ArrayList<Object> result = new ArrayList<>();
        while (true)
        {
            Object t = precedent.next();
            if (t == FqlIterator.sentinel)
                break;
            result.add(t);
        }
        return result;
    }

    @Override
    public void dump(StringBuffer sb) {
        sb.append(" from ... end ");
    }

    @Override
    public void buildMemberName(StringBuffer target) {
        final FqlStatement fqlStatement = clauses.get(0);
        if (fqlStatement instanceof  NestedFromClause) {
            NestedFromClause nfc = (NestedFromClause) fqlStatement;
            nfc.buildMemberName(target);
        } else if (fqlStatement instanceof  FromClause) {
            FromClause nfc = (FromClause) fqlStatement;
            nfc.buildMemberName(target);
        }
    }
}
