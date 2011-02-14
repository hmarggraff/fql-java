package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlEntryPoint;
import org.fqlsource.util.NamedImpl;

import java.util.HashMap;
import java.util.Iterator;

public class ConnectClause extends NamedImpl implements FqlStatement
{
    private final HashMap<String, String> config;

    public ConnectClause(String conn_name, HashMap<String, String> config)
    {
        super(conn_name);
        this.config = config;
    }

    public FqlEntryPoint execute(RunEnv env, Iterator precedent) throws FqlDataException
    {
        env.connect(name, config);
        return null;
    }
}
