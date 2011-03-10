package org.fqlsource.mockdriver;


import org.fqlsource.data.FqlMapContainer;
import org.fqlsource.exec.RunEnv;
import org.fqlsource.util.NamedImpl;

public class MockMapContainer extends NamedImpl implements FqlMapContainer<MockDriverConnection>
{
    private MockDriverConnection connection;


    public MockMapContainer(String name, MockDriverConnection connection)
    {
        super(name);
        this.connection = connection;
    }

    public MockDriverConnection getConnection()
    {
        return connection;
    }

    public Object getObject(RunEnv runEnv, Object from, String member)
    {
        return connection.getDriver().getObject(from, member, this);
    }

    public Object lookup(RunEnv runEnv, Object key)
    {
        return "M" + key.toString();
    }
}
