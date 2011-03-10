package org.fqlsource.exec;

import org.fqlsource.data.FqlConnection;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlStreamContainer;

public class FromClause extends ContainerActivationClause
{
    private final int depth;

    public FromClause(String containerName, String alias, int depth, int connectionIndex)
    {
        super(connectionIndex, alias, containerName);
        this.depth = depth;
    }

    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException
    {
        FqlConnection fqlConnection = env.getConnection(connectionIndex);
        FqlStreamContainer streamContainer = fqlConnection.getStream(streamName);
        env.setStreamAt(depth, streamContainer);
        return streamContainer;
    }

    public int getDepth()
    {
        return depth;
    }

}
