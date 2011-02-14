package org.fqlsource.mockdriver;


import org.fqlsource.data.FqlConnection;

public class MockDriverConnection extends FqlConnection<MockDriver>
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
}
