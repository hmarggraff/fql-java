package org.fqlsource.mockdriver;


import org.fqlsource.data.DefaultFqlConnection;
import org.fqlsource.data.FqlDataException;

public class MockDriverConnection extends DefaultFqlConnection<MockDriver>
{
    private final int count;

    public MockDriverConnection(MockDriver driver, int count)
    {
        super(driver);
        this.count = count;
    }

    public int getCount()
    {
        return count;
    }

    public MockEntryPoint getEntryPoint() throws FqlDataException
    {
        return (MockEntryPoint) getEntryPoint(MockEntryPoint.defaultEntryPointName);
    }

}
