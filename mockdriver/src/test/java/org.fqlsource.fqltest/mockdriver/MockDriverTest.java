package org.fqlsource.fqltest.mockdriver;


import junit.framework.Assert;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.mockdriver.MockDriver;
import org.fqlsource.mockdriver.MockDriverConnection;
import org.fqlsource.mockdriver.MockEntryPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

/**
 * Unit test for simple MockDriver.
 */

public class MockDriverTest
{
    private String defaultEntryPoint = "nowhere";
    MockDriver mockDriver;
    MockDriverConnection conn;

    @Before
    void openConnction() throws FqlDataException
    {
        Properties p = new Properties();
        p.put("driver", "org.fqlsource.mockdriver.MockDriver");
        p.put("count", "1");
        mockDriver = new MockDriver();
        conn = mockDriver.open(p);
    }

    @After
    void closeConnection()
    {
        conn.close();
        mockDriver.close();
    }

    /**
     * Tests if getEntryPoint properly returns the entry point of the mock driver
     *
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
        final MockEntryPoint ep = mockDriver.getEntryPoint(entryPointName, conn);
        return ep;
    }

    /**
     * Tests if getEntryPoint properly detects and signals a non existing entry point
     */
    @Test(expected = FqlDataException.class)
    public void testApp2() throws FqlDataException
    {
        getEntryPoint("fail");
        Assert.fail("Entry point should not exist");
    }

    /**
     * Tests if the mock driver returns data with the expected generated fields.
     *
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
