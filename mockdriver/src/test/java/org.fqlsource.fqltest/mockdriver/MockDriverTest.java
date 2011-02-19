package org.fqlsource.fqltest.mockdriver;

import org.testng.annotations.Test;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.mockdriver.MockDriver;
import org.fqlsource.mockdriver.MockDriverConnection;
import org.fqlsource.mockdriver.MockEntryPoint;

import java.util.Properties;

import org.testng.Assert;

/**
 * Unit test for simple MockDriver.
 */
@Test
public class MockDriverTest
{
    private String defaultEntryPoint = "nowhere";


    /**
     * Rigourous Test :-)
     */
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
