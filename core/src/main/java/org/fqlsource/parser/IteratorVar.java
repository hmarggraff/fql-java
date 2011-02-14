package org.fqlsource.parser;

import org.fqlsource.data.FqlConnection;
import org.fqlsource.util.NamedImpl;

public class IteratorVar extends NamedImpl
{

    FqlConnection connection;

    Object current;

    public FqlConnection getConnection()
    {
        return connection;
    }

    public void setConnection(FqlConnection connection)
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
