package org.fqlsource.data;


import java.util.Date;
import java.util.Properties;

/**
 */
public interface FqlDriver<ConnectionType extends DefaultFqlConnection>
{
    ConnectionType open(Properties props) throws FqlDataException;

    FqlDataSource getSource(String name, FqlConnection fqlConnection) throws FqlDataException;

    Object getObject(Object parent, String fieldName, FqlDataSource dataSource) throws FqlDataException;

    long getLong(Object parent, String fieldName, FqlDataSource dataSource) throws FqlDataException;

    double getDouble(Object parent, String fieldName, FqlDataSource dataSource) throws FqlDataException;

    String getString(Object parent, String fieldName, FqlDataSource dataSource) throws FqlDataException;

    boolean getBoolean(Object parent, String fieldName, FqlDataSource dataSource) throws FqlDataException;

    Date getDate(Object parent, String fieldName, FqlDataSource dataSource) throws FqlDataException;

    void close();
}

