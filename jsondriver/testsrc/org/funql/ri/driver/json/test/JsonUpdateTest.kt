package org.funql.ri.driver.json.test

import java.util.HashMap
import org.testng.annotations.Test
import org.funql.ri.kotlinutil.UpdateTestConnection

class JsonUpdateTest
{

    protected fun runQuery(txt: String, q: String): HashMap<String, KTestUpdater>
    {
        val p = HashMap<String, String>()
        p.put("driver", "JsonDriver")
        p.put("text", txt)
        val conn = JsonConnection("JsonUpdate", p)
        val outConn = UpdateTestConnection("Out")
        val conns:List<FunqlConnection> = listOf(conn,outConn)
        val iterator = FqlParser.runQuery(q, null, conns)!!
        do{
            val el = iterator.next()

        }
        while(FqlIterator.sentinel != el)

        return outConn.updaters
    }

    Test fun updater()
    {
        val out = runQuery("[]", "into Out.test put 'x'")
        val any = out.get("test")!!.data.get(0)
        Assert.assertEquals(any, "x")
    }

    Test fun updater2()
    {
        val out = runQuery("[]", "into Out.test put 'x', 3, z: 7.5")
        val any = out.get("test")!!.data.get(0)
        Assert.assertEquals(any, "x")
    }
}