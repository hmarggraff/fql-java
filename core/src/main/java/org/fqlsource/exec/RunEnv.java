package org.fqlsource.exec;

import org.fqlsource.data.DefaultFqlConnection;
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

    public Object getValue(String member, Object from)
    {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public void connect(String conn_name, HashMap<String, String> config)
    {
        //To change body of created methods use File | Settings | File Templates.
    }
}
