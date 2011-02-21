package org.fqlsource.mockdriver;


import org.fqlsource.data.DefaultFqlConnection;

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

}
