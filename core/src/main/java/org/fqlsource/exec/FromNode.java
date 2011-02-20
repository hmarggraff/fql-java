package org.fqlsource.exec;

import org.fqlsource.data.FqlConnection;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlEntryPoint;

import java.util.Iterator;

public class FromNode implements FqlStatement
{
    private String iteratorName;
    private String connectionName;
    private String datasetName;

    public FromNode(String connection, String datasetName, String iteratorName)
    {
        connectionName = connection;
        this.datasetName = datasetName;
        this.iteratorName = iteratorName;
    }

    public FqlEntryPoint execute(RunEnv env, Iterator precedent) throws FqlDataException
    {
        FqlConnection fqlConnection = env.connections.get(connectionName);
        @SuppressWarnings({"unchecked"})
        FqlEntryPoint entryPoint = fqlConnection.getDriver().getEntryPoint(datasetName, fqlConnection);
        return entryPoint;
    }

    public String getIteratorName()
    {
        return iteratorName;
    }
}
