package org.funql.ri.exec;

import org.funql.ri.data.FunqlConnection;
import org.funql.ri.util.NamedIndex;


public class ProvidedConnection extends NamedIndex
{
    private final FunqlConnection connection;

    public ProvidedConnection(int connectionIndex, FunqlConnection connection)
    {
        super(connection.getName(), connectionIndex);
        this.connection = connection;
    }

    public FunqlConnection getConnection()
    {
        return connection;
    }
}
