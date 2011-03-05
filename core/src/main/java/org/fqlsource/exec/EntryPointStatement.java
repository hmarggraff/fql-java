package org.fqlsource.exec;

import org.fqlsource.data.FqlConnection;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlDataSource;

import java.util.Iterator;

public class EntryPointStatement implements FqlStatement
{
    private String alias;
    private final int entryPointIndex;
    private final int connectionIndex;
    private String entryPointName;

    public EntryPointStatement(String entryPointName, String alias, int entryPointIndex, int connectionIndex)
    {
        this.entryPointName = entryPointName;
        this.alias = alias;
        this.entryPointIndex = entryPointIndex;
        this.connectionIndex = connectionIndex;
    }

    public FqlDataSource execute(RunEnv env, Iterator precedent) throws FqlDataException
    {
        FqlConnection fqlConnection = env.getConnection(connectionIndex);
        FqlDataSource dataSource = fqlConnection.getSource(entryPointName);
        env.setSourceAt(entryPointIndex, dataSource);
        return dataSource;
    }

    public String getAlias()
    {
        return alias;
    }

    public int getEntryPointIndex()
    {
        return entryPointIndex;
    }
}
