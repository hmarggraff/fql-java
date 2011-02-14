package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

public class ConnectionVarNode extends FqlNode
{
    protected final String connectionName;

    public ConnectionVarNode(String connectionName)
    {
        this.connectionName = connectionName;
    }

    public Object getValue(RunEnv env, Object from) throws FqlDataException
    {
        return env.connections.get(connectionName);
    }
}
