package org.fqlsource.mockdriver;

import org.fqlsource.data.FqlConnection;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlDataSource;
import org.fqlsource.data.FqlDriver;

import java.util.Date;
import java.util.Properties;

/**
 * Hello world!
 */
public class MockDriver implements FqlDriver<MockDriverConnection>
{


    public MockDriverConnection open(Properties props) throws FqlDataException
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

    public MockDataSource getSource(String name, FqlConnection fqlConnection) throws FqlDataException
    {
        MockDriverConnection conn = (MockDriverConnection) fqlConnection;
        if (name.equals("nowhere"))
        {
            return new MockDataSource(conn, conn.getCount());
        }
        else if (Character.isJavaIdentifierStart(name.charAt(0)) && Character.isDigit(name.charAt(1)))
        {
            long count = letterNum(name);
            return new MockDataSource(conn, (int) count);
        }
        throw new FqlDataException("Entry Point Named " + name + " does not exist");
    }

    public Object getObject(Object parent, String fieldName, FqlDataSource dataSource)
    {
        return parent.toString()+ '.' + fieldName;
    }

    public long getLong(Object parent, String fieldName, FqlDataSource dataSource) throws FqlDataException
    {
        return letterNum(fieldName);
    }

    private long letterNum(String fieldName)
      throws FqlDataException
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

    public double getDouble(Object parent, String fieldName, FqlDataSource dataSource) throws FqlDataException
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

    public String getString(Object parent, String fieldName, FqlDataSource dataSource)
    {
        return fieldName;
    }

    public boolean getBoolean(Object parent, String fieldName, FqlDataSource dataSource)
    {
        return "yes".equals(fieldName);
    }

    public Date getDate(Object parent, String fieldName, FqlDataSource dataSource) throws FqlDataException
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
}
