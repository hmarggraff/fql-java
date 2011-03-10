package org.fqlsource.data;


import java.util.Date;
import java.util.Properties;

/**
 */
public interface FqlDriver<ConnectionType extends DefaultFqlConnection>
{
    ConnectionType open(Properties props) throws FqlDataException;

    FqlStreamContainer getStream(String name, ConnectionType fqlConnection) throws FqlDataException;

    Object getObject(Object parent, String fieldName, FqlContainer container) throws FqlDataException;

    long getLong(Object parent, String fieldName, FqlContainer container) throws FqlDataException;

    double getDouble(Object parent, String fieldName, FqlContainer container) throws FqlDataException;

    String getString(Object parent, String fieldName, FqlContainer container) throws FqlDataException;

    boolean getBoolean(Object parent, String fieldName, FqlContainer container) throws FqlDataException;

    Date getDate(Object parent, String fieldName, FqlContainer container) throws FqlDataException;

    void close();

    FqlMapContainer getMap(String containerName, ConnectionType fqlConnection);
}

