package org.funql.ri.exec.node;
import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.exec.node.EntryPointSlot;
import org.funql.ri.exec.node.FqlNodeInterface;

/**
 * Created by hmf on 17.07.2014.
 */
public class JoinClause  implements FqlStatement
{
    private final String root;
    private final String alias;
    private final EntryPointSlot entryPointSlot;
    private final FqlNodeInterface joinExpression;

    public JoinClause(String root, String alias, EntryPointSlot entryPointSlot, FqlNodeInterface joinExpression)
    {
        this.root = root;
        this.alias = alias;
        this.entryPointSlot = entryPointSlot;
        this.joinExpression = joinExpression;
    }

    @Override
    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException
    {
        return null;
    }
}
