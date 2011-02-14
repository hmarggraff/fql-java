package org.fqlsource.fqltest.mockdriver;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.mockdriver.MockDriver;
import org.fqlsource.mockdriver.MockDriverConnection;
import org.fqlsource.mockdriver.MockEntryPoint;

import java.util.Properties;

/**
 * Unit test for simple MockDriver.
 */
public class MockDriverTest extends TestCase
{
    private String defaultEntryPoint = "nowhere";

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MockDriverTest(String testName)
    {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(MockDriverTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws FqlDataException
    {
        defaultEntryPoint = "nowhere";
        assertNotNull(getEntryPoint(defaultEntryPoint));
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
            fail("Entry point should not exist");
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
            assertTrue(driver.getBoolean(it, "yes", entryPoint));
            assertFalse(driver.getBoolean(it, "no", entryPoint));
            assertEquals(driver.getLong(it, "L" + Integer.toString(count), entryPoint), count);
            assertEquals(driver.getString(it, "S" + count, entryPoint), "S" + count);
            assertEquals(driver.getDate(it, "D" + count, entryPoint).getTime(), count);
            count++;
        }
        System.out.println("Basic fields exist");
    }
}
