package org.funql.ri.exec.node;
import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.FunqlConnection;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;

/**
 * Created by hmf on 17.07.2014.
 */
public class JoinClause  implements FqlStatement
{
    private final String containerName;
    private final String alias;
    private final EntryPointSlot connectionSlot;
    private final FqlNodeInterface joinExpression;

    public JoinClause(String containerName, String alias, EntryPointSlot connectionSlot, FqlNodeInterface joinExpression)
    {
        this.containerName = containerName;
        this.alias = alias;
        this.connectionSlot = connectionSlot;
        this.joinExpression = joinExpression;
    }

    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException {
        FunqlConnection funqlConnection = env.getConnection(connectionSlot.getIndex());
        FqlIterator listContainer = funqlConnection.getIterator(containerName);
        // if joinExpression is equiJoin process hash join otherwise full product
        return listContainer;
    }
}
