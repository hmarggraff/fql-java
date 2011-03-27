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

    public FqlStreamContainer getStream(String streamName) throws FqlDataException
    {
        return driver.getStream(streamName, this);
    }

    public FqlMapContainer getMap(String containerName) throws FqlDataException
    {
        return driver.getMap(containerName, this);
    }

    public Object getObject(Object from, String member, FqlStreamContainer streamContainer) throws FqlDataException
    {
        return driver.getObject(from, member, streamContainer);
    }
}
