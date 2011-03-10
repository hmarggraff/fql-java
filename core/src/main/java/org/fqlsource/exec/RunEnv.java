package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.FqlConnection;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlMapContainer;
import org.fqlsource.data.FqlStreamContainer;

import java.util.HashMap;

public class RunEnv
{
    Object[] parameterValues;
    /**
     * the list of open connections
     */
    FqlConnection[] connections;
    /**
     * List of secondary data sources with lookup access
     */
    FqlMapContainer[] lookups;
    /**
     * Stack of iterator streams, depth is equivalent to the nesting level of the query
     */
    FqlStreamContainer[] streams;

    public RunEnv(int connectionCount, int streamDepth, int secondaries, Object[] parameterValues)
    {
        connections = new FqlConnection[connectionCount];
        streams = new FqlStreamContainer[streamDepth];
        lookups = new FqlMapContainer[secondaries];
        this.parameterValues = parameterValues;
    }

    public Object getVariable(int parameterIndex)
    {
        return parameterValues[parameterIndex];
    }

    public Object getValue(String member, Object from, int depth) throws FqlDataException
    {
        Object object = streams[depth].getObject(this, from, member);
        return object;
    }

    public void connect(String conn_name, HashMap<String, String> config)
    {
        throw new NotYetImplementedError();
    }

    public FqlStreamContainer iteratorEntryPoint(int depth)
    {
        return streams[depth];
    }

    FqlMapContainer getEntryPoint(int entryPointIndex)
    {
        return lookups[entryPointIndex];
    }

    public FqlConnection getConnection(int connectionIndex)
    {
        return connections[connectionIndex];
    }

    public void setStreamAt(int depth, FqlStreamContainer streamContainer)
    {
        streams[depth] = streamContainer;
    }

    public void setMapContainer(int index, FqlMapContainer container)
    {
        lookups[index] = container;
    }

    public void setConnectionAt(int index, FqlConnection conn)
    {
        connections[index] = conn;
    }
}
