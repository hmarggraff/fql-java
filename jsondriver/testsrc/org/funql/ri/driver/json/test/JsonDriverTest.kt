package org.funql.ri.driver.json.test

import org.testng.annotations.Test
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.data.FqlDataException
import org.funql.ri.data.NamedValues
import java.util.HashMap

class JsonDriverTest
{

    fun openConnction(txt: String): JsonConnection
    {
        val p = HashMap<String, String>()
        p.put("driver", "JsonDriver")
        p.put("text", txt)
        //throw Exception("bal")
        return JsonConnection("Json", p)
    }

    /**
     * Tests if getList properly returns the entry point of the mock driver
     *
     * @throws org.fqlsource.data.FqlDataException Thrown if entry point access fails in driver
     */
    Test fun testApp()
    {
        val conn = openConnction("[]")
        Assert.assertNotNull(conn.getIterator("top"))
        conn.close()
    }

    /**
     * Tests if getList properly returns the entry point of the mock driver
     *
     * @throws org.fqlsource.data.FqlDataException Thrown if entry point access fails in driver
     */
    Test fun testApp3()
    {
        val conn = openConnction("[{a: b, c: {d:e}}]")
        val map: FqlMapContainer = conn.useMap("text", listOf<String>("a"), true)!!
        val lookup = map.lookup("a")
        Assert.assertEquals(lookup, "b")
        conn.close()

    }
    Test fun testNestedLookup()
    {
        val conn = openConnction("[{c: {d:e}}]")
        val map: FqlMapContainer = conn.useMap("text", listOf<String>("c","d"), true)!!
        val lookup = map.lookup("e")
        Assert.assertEquals(lookup, "b")
        conn.close()

    }

    /**
     * Tests if getList properly detects and signals a non existing entry point
     * @throws org.fqlsource.data.FqlDataException expected
     */
    Test
            fun testApp2()
    {
        val conn = openConnction("{a: b, c: d}")
        try {
            conn.getIterator("ExceptionTesting")
            Assert.fail("Entry point should not exist")
        } catch (ex: FqlDataException) {
            // ok
        }
        finally{
            conn.close()
        }
    }

    /**
     * Tests if the mock driver returns data with the expected generated fields.
     *
     */
    Test fun testAppFields()
    {
        val conn = openConnction("[2,3,5,7]")
        val stream = conn.getIterator("top")!!

        var count: Int = 0
        while (true)
        {
            val it = stream.next()

            if (it == FqlIterator.sentinel) break
            else if (it is NamedValues && it.getValues()!![0] is Int ) count = count + it.getValues()!![0] as Int
            else throw ClassCastException("json array iterator returns ${it.javaClass} when NamedValues of int was expected: ${it.toString()}")
        }
        Assert.assertEquals(count, 17)
    }

}