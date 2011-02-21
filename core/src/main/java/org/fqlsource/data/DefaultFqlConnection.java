package org.fqlsource.data;

/**
 */
public class DefaultFqlConnection<FqlDriverType extends FqlDriver> implements FqlConnection<FqlDriverType>
{
    FqlDriverType driver;

    public DefaultFqlConnection(FqlDriverType driver)
    {
        this.driver = driver;
    }

    public FqlDriverType getDriver()
    {
        return driver;
    }

    public void close()
    {
        //nothing to do
    }
}
