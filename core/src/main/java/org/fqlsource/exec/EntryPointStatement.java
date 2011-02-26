package org.fqlsource.exec;

import org.fqlsource.data.DefaultFqlConnection;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlEntryPoint;

import java.util.Iterator;

public class EntryPointStatement implements FqlStatement
{
    private String alias;
    private final int entryPointIndex;
    private final int connectionIndex;
    private String connectionName;
    private String entryPointName;

    public EntryPointStatement(String entryPointName, String alias, int entryPointIndex, int connectionIndex)
    {
        this.entryPointName = entryPointName;
        this.alias = alias;
        this.entryPointIndex = entryPointIndex;
        this.connectionIndex = connectionIndex;
    }

    public FqlEntryPoint execute(RunEnv env, Iterator precedent) throws FqlDataException
    {
        DefaultFqlConnection fqlConnection = env.connections.get(connectionName);
        @SuppressWarnings({"unchecked"})
        FqlEntryPoint entryPoint = fqlConnection.getDriver().getEntryPoint(entryPointName, fqlConnection);
        return entryPoint;
    }

    public String getAlias()
    {
        return alias;
    }
}
