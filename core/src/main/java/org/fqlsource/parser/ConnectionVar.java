package org.fqlsource.parser;

import org.fqlsource.data.FqlConnection;
import org.fqlsource.util.NamedImpl;

public class ConnectionVar extends NamedImpl
{

    FqlConnection connection;

    public ConnectionVar(String name, FqlConnection connection)
    {
        super(name);
        this.connection = connection;
    }

    public FqlConnection getConnection()
    {
        return connection;
    }

    IteratorVar getEntryPoint(String name)
    {
        return new IteratorVar(name);
    }
}
