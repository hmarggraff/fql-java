package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.*;

import java.util.HashMap;

public class RunEnv
{
    Object[] parameterValues;
    FqlConnection[] connections;
    FqlDataSource[] dataSources;

    public RunEnv(int connectionCount, int entryPointCount, Object[] parameterValues)
    {
        connections = new FqlConnection[connectionCount];
        dataSources = new FqlDataSource[entryPointCount];
        this.parameterValues = parameterValues;
    }

    public Object getVariable(int parameterIndex)
    {
        return parameterValues[parameterIndex];
    }

    public Object getValue(String member, Object from, int entryPointIndex) throws FqlDataException
    {
        Object object = dataSources[entryPointIndex].getObject(this, from, member);
        return object;
    }

    public void connect(String conn_name, HashMap<String, String> config)
    {
        throw new NotYetImplementedError();
    }

    public FqlDataSource iteratorEntryPoint()
    {
        return dataSources[dataSources.length-1];
    }

    FqlDataSource getEntryPoint(int entryPointIndex)
    {
        return dataSources[entryPointIndex];
    }

    public FqlConnection getConnection(int connectionIndex)
    {
        return connections[connectionIndex];
    }

    public void setSourceAt(int entryPointIndex, FqlDataSource dataSource)
    {
        dataSources[entryPointIndex] = dataSource;
    }

    public void setConnectionAt(int index, FqlConnection conn)
    {
        connections[index] = conn;
    }
}
