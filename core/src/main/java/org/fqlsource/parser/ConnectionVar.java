package org.fqlsource.parser;

import org.fqlsource.data.DefaultFqlConnection;
import org.fqlsource.util.NamedImpl;

public class ConnectionVar extends NamedImpl
{

    DefaultFqlConnection connection;

    public ConnectionVar(String name, DefaultFqlConnection connection)
    {
        super(name);
        this.connection = connection;
    }

    public DefaultFqlConnection getConnection()
    {
        return connection;
    }

    IteratorVar getEntryPoint(String name)
    {
        return new IteratorVar(name);
    }
}
