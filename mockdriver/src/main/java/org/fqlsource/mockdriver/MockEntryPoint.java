package org.fqlsource.mockdriver;


import org.fqlsource.data.FqlEntryPoint;

import java.util.Iterator;

public class MockEntryPoint implements FqlEntryPoint<MockDriverConnection>
{
    public static String defaultEntryPointName = "nowhere";
    final int count;
    private MockDriverConnection connection;

    public MockEntryPoint(MockDriverConnection connection, int count)
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
}
