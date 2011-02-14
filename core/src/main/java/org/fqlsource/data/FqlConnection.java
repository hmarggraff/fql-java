package org.fqlsource.data;

/**
 */
public class FqlConnection<FqlDriverType extends FqlDriver>
{
    FqlDriverType driver;

    public FqlConnection(FqlDriverType driver)
    {
        this.driver = driver;
    }

    public FqlDriverType getDriver()
    {
        return driver;
    }

}
