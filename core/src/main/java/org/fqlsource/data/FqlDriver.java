package org.fqlsource.data;


import java.util.Date;
import java.util.Properties;

/**
 */
public interface FqlDriver<ConnectionType extends FqlConnection, EntryPointType extends FqlEntryPoint>
{
    ConnectionType open(Properties props) throws FqlDataException;

    EntryPointType getEntryPoint(String name, ConnectionType fqlConnection) throws FqlDataException;

    Object getObject(Object parent, String fieldName, EntryPointType entryPoint) throws FqlDataException;

    long getLong(Object parent, String fieldName, EntryPointType entryPoint) throws FqlDataException;

    double getDouble(Object parent, String fieldName, EntryPointType entryPoint) throws FqlDataException;

    String getString(Object parent, String fieldName, EntryPointType entryPoint) throws FqlDataException;

    boolean getBoolean(Object parent, String fieldName, EntryPointType entryPoint) throws FqlDataException;

    Date getDate(Object parent, String fieldName, EntryPointType entryPoint) throws FqlDataException;
}

