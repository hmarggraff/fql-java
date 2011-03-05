package org.fqlsource.data;

import org.fqlsource.util.NamedImpl;

/**
 */
public class DefaultFqlConnection<FqlDriverType extends FqlDriver> extends NamedImpl implements FqlConnection
{
    protected FqlDriverType driver;

    public DefaultFqlConnection(FqlDriverType driver, String name)
    {
        super(name);
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
    public FqlDataSource getSource(String sourceName) throws FqlDataException
    {
        return driver.getSource(sourceName, this);
    }

    public Object getObject(Object from, String member, FqlDataSource dataSource) throws FqlDataException
    {
        return driver.getObject(from,member, dataSource);
    }
}
