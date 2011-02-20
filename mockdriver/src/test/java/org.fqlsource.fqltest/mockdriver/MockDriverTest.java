package org.fqlsource.fqltest.mockdriver;


import junit.framework.Assert;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.mockdriver.MockDriver;
import org.fqlsource.mockdriver.MockDriverConnection;
import org.fqlsource.mockdriver.MockEntryPoint;

import java.util.Properties;

import org.junit.Test;

/**
 * Unit test for simple MockDriver.
 */

public class MockDriverTest
{
    private String defaultEntryPoint = "nowhere";


    /**
     * Tests if getEntryPoint properly returns the entry point of the mock driver
     * @throws FqlDataException Thrown if entry point access fails in driver
     */
    @Test
    public void testApp() throws FqlDataException
    {
        defaultEntryPoint = "nowhere";
        Assert.assertNotNull(getEntryPoint(defaultEntryPoint));
        //assertTrue(true);
    }

    private MockEntryPoint getEntryPoint(final String entryPointName) throws FqlDataException
    {
        Properties p = new Properties();
        p.put("driver", "org.fqlsource.mockdriver.MockDriver");
        p.put("count", "1");
        final MockDriver mockDriver = new MockDriver();
        final MockDriverConnection open = mockDriver.open(p);
        final MockEntryPoint ep = mockDriver.getEntryPoint(entryPointName, open);
        return ep;
    }

    /**
     * Tests if getEntryPoint properly detects and signals a non existing entry point
     */
    @Test
    public void testApp2()
    {
        try
        {
            getEntryPoint("fail");
            Assert.fail("Entry point should not exist");
        }
        catch (FqlDataException e)
        {
            final String message = e.getMessage();
            System.out.println("Expected exception: " + message);
            return;
        }
    }

    /**
     * Tests if the mock driver returns data with the expected generated fields.
     * @throws FqlDataException
     */
    @Test
    public void testAppFields() throws FqlDataException
    {
        final MockEntryPoint entryPoint = getEntryPoint(defaultEntryPoint);
        final MockDriver driver = entryPoint.getConnection().getDriver();

        int count = 1;
        for (Object it : entryPoint)
        {
            Assert.assertTrue(driver.getBoolean(it, "yes", entryPoint));
            Assert.assertFalse(driver.getBoolean(it, "no", entryPoint));
            Assert.assertEquals(driver.getLong(it, "L" + Integer.toString(count), entryPoint), count);
            Assert.assertEquals(driver.getString(it, "S" + count, entryPoint), "S" + count);
            Assert.assertEquals(driver.getDate(it, "D" + count, entryPoint).getTime(), count);
            count++;
        }
        System.out.println("Basic fields exist");
    }
}
