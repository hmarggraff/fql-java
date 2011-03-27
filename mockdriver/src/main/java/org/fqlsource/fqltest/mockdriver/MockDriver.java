package org.fqlsource.fqltest.mockdriver;

import org.fqlsource.data.*;

import java.util.Date;
import java.util.Properties;

public class MockDriver implements FqlDriver
{


    public FqlConnection open(Properties props) throws FqlDataException
    {
        final String countStr = props.getProperty("count");
        int count;
        try
        {
            count = Integer.parseInt(countStr);
            return new MockDriverConnection(this, count);
        }
        catch (NumberFormatException e)
        {
            throw new FqlDataException("Count is not a natural number", e);
        }
    }

    public FqlStreamContainer getStream(String name, FqlConnection conn) throws FqlDataException
    {
        final MockDriverConnection mockDriverConnection = (MockDriverConnection) conn;
        if (name.equals("nowhere"))
        {
            return new MockStreamContainer(mockDriverConnection, mockDriverConnection.getCount());
        }
        else if (Character.isJavaIdentifierStart(name.charAt(0)) && Character.isDigit(name.charAt(1)))
        {
            long count = letterNum(name);
            return new MockStreamContainer(mockDriverConnection, (int) count);
        }
        throw new FqlDataException("Entry Point Named " + name + " does not exist");
    }

    public Object getObject(Object parent, String fieldName, FqlContainer container)
    {
        return parent.toString() + '.' + fieldName;
    }

    public long getLong(Object parent, String fieldName, FqlContainer streamContainer) throws FqlDataException
    {
        return letterNum(fieldName);
    }

    private long letterNum(String fieldName) throws FqlDataException
    {
        String vTxt = fieldName.substring(1);
        try
        {
            final long ret = Long.parseLong(vTxt);
            return ret;
        }
        catch (NumberFormatException fex)
        {
            throw new FqlDataException("Mockdriver failed to parse long from fieldname. Should be 'E'+ value", fex);
        }
    }

    public double getDouble(Object parent, String fieldName, FqlContainer streamContainer) throws FqlDataException
    {
        String vTxt = fieldName.substring(1);
        vTxt = vTxt.replace('_', '.');
        try
        {
            final long ret = Long.parseLong(vTxt);
            return ret;
        }
        catch (NumberFormatException fex)
        {
            throw new FqlDataException("Mockdriver failed to parse long from fieldname. Should be 'C'+ value_decimal", fex);
        }
    }

    public String getString(Object parent, String fieldName, FqlContainer streamContainer)
    {
        return fieldName;
    }

    public boolean getBoolean(Object parent, String fieldName, FqlContainer streamContainer)
    {
        return "yes".equals(fieldName);
    }

    public Date getDate(Object parent, String fieldName, FqlContainer streamContainer) throws FqlDataException
    {
        String vTxt = fieldName.substring(1);
        try
        {
            final long ret = Long.parseLong(vTxt);
            return new Date(ret);
        }
        catch (NumberFormatException fex)
        {
            throw new FqlDataException("Mockdriver failed to parse Date from fieldname. Should be 'D'+ value", fex);
        }
    }

    public void close()
    {
        // nothing
    }

    public FqlMapContainer getMap(String containerName, FqlConnection fqlConnection)
    {
        return new MockMapContainer(containerName, (MockDriverConnection) fqlConnection);
    }
}
