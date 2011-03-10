package org.fqlsource.mockdriver;


import org.fqlsource.data.FqlStreamContainer;
import org.fqlsource.exec.RunEnv;
import org.fqlsource.util.Named;

public class MockStreamContainer implements FqlStreamContainer<MockDriverConnection>
{
    public static String defaultEntryPointName = "nowhere";
    final int count;
    private MockDriverConnection connection;
    int at = 0;

    public MockStreamContainer(MockDriverConnection connection, int count)
    {
        this.connection = connection;
        this.count = count;
    }

    public boolean hasNext()
    {
        return at < count;
    }

    public Object next()
    {
        at++;
        return new Integer(at);

    }

    public MockDriverConnection getConnection()
    {
        return connection;
    }

    public Object getObject(RunEnv runEnv, Object from, String member)
    {
        return connection.getDriver().getObject(from, member, this);
    }

    public String getName()
    {
        return defaultEntryPointName;
    }

    public int compareTo(Named n)
    {
        return getName().compareTo(n.getName());
    }
}
