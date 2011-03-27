package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlIterator;
import org.fqlsource.data.RunEnv;
import org.fqlsource.util.NamedIndex;

import java.util.HashMap;

public class ConnectClause extends NamedIndex implements FqlStatement
{
    protected final HashMap<String, String> config;

    public ConnectClause(String conn_name, int connectionIndex, HashMap<String, String> config)
    {
        super(conn_name, connectionIndex);
        this.config = config;
    }

    public FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException
    {
        env.connect(name, config);
        return null;
    }
}
