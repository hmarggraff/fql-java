package org.fqlsource.data;

import org.fqlsource.util.Named;

/**
 */
public interface FqlConnection extends Named
{

    String default_provided_connection_name = "provided_connection";

    public void close();

    FqlStreamContainer getStream(String streamName) throws FqlDataException;

    FqlMapContainer getMap(String containerName) throws FqlDataException;
}
