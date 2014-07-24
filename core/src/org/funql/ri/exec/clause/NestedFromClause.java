package org.funql.ri.exec.clause;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.exec.node.FqlNodeInterface;

/**
 * Date: 12.05.13 14:33
 */
public class NestedFromClause implements FqlStatement {
    private FqlNodeInterface fromNode;

    public NestedFromClause(FqlNodeInterface fromNode) {
        this.fromNode = fromNode;
    }

    public void dump(StringBuffer sb) {
        sb.append(" from ");
        fromNode.dump(sb);
        sb.append(" end ");
    }

    @Override
    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException {
        Object fromValue = fromNode.getValue(env, fromNode);
        if (fromValue instanceof FqlIterator) return (FqlIterator) fromValue;
        throw new FqlDataException("Not an iterator in from clause of nested query.", fromNode.getRow(), fromNode.getCol());
    }

    public void buildMemberName(StringBuffer target) {
        fromNode.buildMemberName(target);
    }
}
