package org.fqlsource.data;

import org.fqlsource.util.Named;

/**
 */
public interface FqlConnection extends Named
{

    public void close();

    FqlDataSource getSource(String sourceName) throws FqlDataException;
}
