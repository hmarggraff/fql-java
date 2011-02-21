package org.fqlsource.data;

/**
 */
public interface FqlConnection<FqlDriverType extends FqlDriver>
{

    public FqlDriverType getDriver();

    public void close();
}
