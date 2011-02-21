package org.fqlsource.parser;

import org.fqlsource.data.DefaultFqlConnection;
import org.fqlsource.util.NamedImpl;

public class IteratorVar extends NamedImpl
{

    DefaultFqlConnection connection;

    Object current;

    public DefaultFqlConnection getConnection()
    {
        return connection;
    }

    public void setConnection(DefaultFqlConnection connection)
    {
        this.connection = connection;
    }

    public Object getCurrent()
    {
        return current;
    }

    public void setCurrent(Object current)
    {
        this.current = current;
    }

    public IteratorVar(String name)
    {
        super(name);
    }
}
