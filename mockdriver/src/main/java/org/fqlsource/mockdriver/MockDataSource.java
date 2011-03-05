package org.fqlsource.mockdriver;


import org.fqlsource.data.FqlDataSource;
import org.fqlsource.exec.RunEnv;
import org.fqlsource.util.Named;

import java.util.Iterator;

public class MockDataSource implements FqlDataSource<MockDriverConnection>
{
    public static String defaultEntryPointName = "nowhere";
    final int count;
    private MockDriverConnection connection;

    public MockDataSource(MockDriverConnection connection, int count)
    {
        this.connection = connection;
        this.count = count;
    }

    public Iterator iterator()
    {
        return new Iterator()
        {
            int at = 0;

            public boolean hasNext()
            {
                return at < count;
            }

            public Object next()
            {
                at++;
                return new Integer(at);

            }

            public void remove()
            {

            }
        };
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
