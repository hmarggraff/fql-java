package org.fqlsource.parser;

import org.fqlsource.data.DefaultFqlConnection;
import org.fqlsource.data.FqlConnection;
import org.fqlsource.util.NamedImpl;

public class IteratorVar extends NamedImpl
{

    FqlConnection connection;
    int entryPointIndex; // key for fast lookup in RunEnv

    Object current;

    public FqlConnection getConnection()
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
