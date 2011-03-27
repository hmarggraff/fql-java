package org.fqlsource.exec;

import org.fqlsource.data.*;
import org.fqlsource.util.NamedIndex;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ConnectClause extends NamedIndex implements FqlStatement
{
    protected final Map<String, String> config;
    private final int row;
    private final int col;


    public ConnectClause(String conn_name, int connectionIndex, HashMap<String, String> config, int row, int col)
    {
        super(conn_name, connectionIndex);
        this.config = config;
        this.row = row;
        this.col = col;
    }

    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException
    {
        final String connectionClassName = config.get("driver");
        try
        {
            final Class<?> connectionClass = Class.forName(connectionClassName);
            final Object connectionObject = connectionClass.newInstance();
            final FqlConnection connection = (FqlConnection) connectionObject;
            connection.init(config);
            env.setConnectionAt(index, connection);
        }
        catch (Exception e)
        {
            throw new FqlDataException("Driver named " + connectionClassName + " could not be loaded.", e, row, col);
        }
        return null;
    }
}
