package org.fqlsource.exec;

import org.fqlsource.data.FqlConnection;
import org.fqlsource.util.NamedIndex;


public class ProvidedConnection extends NamedIndex
{
    private final FqlConnection connection;

    public ProvidedConnection(int connectionIndex, FqlConnection connection)
    {
        super(connection.getName(), connectionIndex);
        this.connection = connection;
    }

    public FqlConnection getConnection()
    {
        return connection;
    }
}
