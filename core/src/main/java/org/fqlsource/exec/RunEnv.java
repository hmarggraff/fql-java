package org.fqlsource.exec;

import org.fqlsource.data.FqlConnection;
import org.fqlsource.data.FqlQueryParameter;

import java.util.HashMap;
import java.util.Map;

public class RunEnv
{
    FqlConnection connection;
    public final Map<String, Object> parameterValues = new HashMap<String, Object>();
    public final HashMap<String, FqlConnection> connections = new HashMap<String, FqlConnection>();

    public Object getVariable(FqlQueryParameter name)
    {
        return parameterValues.get(name);
    }

    public Object getValue(String member, Object from)
    {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public void connect(String conn_name, HashMap<String, String> config)
    {
        //To change body of created methods use File | Settings | File Templates.
    }
}
