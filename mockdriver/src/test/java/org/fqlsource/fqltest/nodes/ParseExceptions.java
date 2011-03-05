package org.fqlsource.fqltest.nodes;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.mockdriver.MockDriver;
import org.fqlsource.mockdriver.MockDriverConnection;
import org.fqlsource.parser.FqlParseException;
import org.fqlsource.parser.FqlParser;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(value = Parameterized.class)
public class ParseExceptions
{
    String query;
    static MockDriverConnection conn;

    @Parameterized.Parameters
    public static Collection<Object[]> data()
    {
        String[] data = {"from\n\"abc\"", "use\n\"abc\"", "use b\nas from a ", "use b", "use b as \"bla\""};
        ArrayList<Object[]> ret = new ArrayList<Object[]>();
        for (String s : data)
        {
            ret.add(new String[]{s});
        }
        return ret;
    }

    public ParseExceptions(String query)
    {
        this.query = query;
    }

    @BeforeClass
    public static void bc() throws FqlDataException
    {
        Properties p = new Properties();
        p.put("driver", "org.fqlsource.mockdriver.MockDriver");
        p.put("count", "1");
        final MockDriver mockDriver = new MockDriver();
        conn = mockDriver.open(p);
    }

    @Test(expected = FqlParseException.class)
    public void test2() throws Exception
    {
        try
        {
            FqlParser.parse(query);
        }
        catch (FqlParseException e)
        {
            System.out.println("Message: " + e.getMessageLong());
            throw e;
        }
        Assert.fail("Query should fail with FqlParseException: " + query);
    }


    @AfterClass
    public static void log()
    {
        // nothing
    }
}