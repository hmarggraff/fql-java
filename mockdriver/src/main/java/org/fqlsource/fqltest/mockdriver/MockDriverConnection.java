package org.fqlsource.fqltest.mockdriver;


import org.fqlsource.data.DefaultFqlConnection;
import org.fqlsource.data.FqlConnection;

public class MockDriverConnection extends DefaultFqlConnection<MockDriver>
{
    private final int count;

    public MockDriverConnection(MockDriver driver, int count)
    {
        super(driver, FqlConnection.default_provided_connection_name);
        this.count = count;
    }

    public int getCount()
    {
        return count;
    }

    /*
    public MockStreamContainer getMockStream() throws FqlDataException
    {
        return (MockStreamContainer) getStream(MockStreamContainer.defaultEntryPointName);
    }
    */

}
