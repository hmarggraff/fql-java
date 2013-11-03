/**
 * Created by hmf on 03.11.13.
 */

import java.util.HashMap
import org.funql.ri.jsondriver.JsonConnection
import org.funql.ri.parser.FqlParser
import org.funql.ri.test.util.dump
import org.testng.Assert

open class JsonTestBase {
    protected fun run(txt: String, q: String, expect: Any)
    {
        val p = HashMap<String, String>()
        p.put("driver", "JsonDriver")
        p.put("text", txt)
        val conn = JsonConnection("Json", p)
        val iter = FqlParser.runQuery(q, null, conn)!!
        val nextVal = iter.next()
        val strRes = dump(nextVal)
        Assert.assertEquals(strRes, expect)
        conn.close()
    }

}
