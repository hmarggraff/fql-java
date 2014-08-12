package org.funql.ri.driver.json.test

import java.util.HashMap
import org.funql.ri

open class JsonTestBase {
    protected fun runQuery(txt: String, q: String, expect: Any)
    {
        val p = HashMap<String, String>()
        p.put("driver", "JsonDriver")
        p.put("text", txt)
        val conn = JsonConnection("Json", p)
        val iter = FqlParser.runQuery(q, null, conn)!!
        val nextVal = iter.next()
        val strRes = ri.test.util.dump(nextVal)
        Assert.assertEquals(strRes, expect)
        conn.close()
    }

}