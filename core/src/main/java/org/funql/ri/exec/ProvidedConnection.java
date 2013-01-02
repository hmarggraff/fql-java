package org.funql.ri.exec;

import org.funql.ri.data.FqlConnection;
import org.funql.ri.util.NamedIndex;


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
