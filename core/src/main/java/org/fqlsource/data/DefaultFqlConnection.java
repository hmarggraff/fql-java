package org.fqlsource.data;

/**
 */
public class DefaultFqlConnection<FqlDriverType extends FqlDriver> implements FqlConnection<FqlDriverType>
{
    protected FqlDriverType driver;

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
    public FqlEntryPoint getEntryPoint(String name) throws FqlDataException
    {
        return driver.getEntryPoint(name, this);
    }


}
