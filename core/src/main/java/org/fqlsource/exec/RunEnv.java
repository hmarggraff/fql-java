package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.DefaultFqlConnection;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlEntryPoint;
import org.fqlsource.data.FqlQueryParameter;

import java.util.HashMap;
import java.util.Map;

public class RunEnv
{
    DefaultFqlConnection connection;
    public final Map<String, Object> parameterValues = new HashMap<String, Object>();
    public final HashMap<String, DefaultFqlConnection> connections = new HashMap<String, DefaultFqlConnection>();

    public Object getVariable(FqlQueryParameter name)
    {
        return parameterValues.get(name);
    }

    public Object getValue(String member, Object from, FqlEntryPoint dataset) throws FqlDataException
    {
        Object object = dataset.getConnection().getDriver().getObject(from, member, dataset);
        return object;
    }

    public void connect(String conn_name, HashMap<String, String> config)
    {
        throw new NotYetImplementedError();
    }

    public FqlEntryPoint interatorEntryPoint()
    {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
