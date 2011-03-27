package org.fqlsource.exec;

import org.fqlsource.data.*;

public class UseClause extends ContainerActivationClause
{
    private final int runtimeIndex;

    public UseClause(String containerName, String alias, int runtimeIndex, int connectionIndex)
    {
        super(connectionIndex, alias, containerName);
        this.runtimeIndex = runtimeIndex;
    }

    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException
    {
        FqlConnection fqlConnection = env.getConnection(connectionIndex);
        FqlMapContainer mapContainer = fqlConnection.getMap(streamName);
        env.setMapContainer(runtimeIndex, mapContainer);
        return null;
    }

    public int getRuntimeIndex()
    {
        return runtimeIndex;
    }

}
