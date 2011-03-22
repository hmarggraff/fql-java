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

    /**
     * the current nesting level. 0 for the top level
     */
    int currentNesting;

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

    public Object getValue(String member, Object from, int index) throws FqlDataException
    {
        Object object = lookups[index].getObject(this, from, member);
        return object;
    }

    public void connect(String conn_name, HashMap<String, String> config)
    {
        throw new NotYetImplementedError();
    }


    FqlMapContainer getEntryPoint(int entryPointIndex)
    {
        return lookups[entryPointIndex];
    }

    public FqlConnection getConnection(int connectionIndex)
    {
        return connections[connectionIndex];
    }

    public void setMapContainer(int index, FqlMapContainer container)
    {
        lookups[index] = container;
    }

    public void setConnectionAt(int index, FqlConnection conn)
    {
        connections[index] = conn;
    }

    public Object getValueFromIterator(String memberName, Object from)
    {
        Object object = lookups[currentNesting].getObject(this, from, memberName);
        return object;
    }
}
