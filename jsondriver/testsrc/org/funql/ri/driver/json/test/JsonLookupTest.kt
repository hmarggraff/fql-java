package org.funql.ri.driver.json.test

import org.testng.annotations.Test
import java.util.HashMap
import org.funql.ri

class JsonLookupTest
{
    fun run(txt: String, q: String, expect: Any)
    {
        val p = HashMap<String, String>()
        p.put("driver", "JsonDriver")
        p.put("text", txt)
        val conn = JsonConnection("Json", p)
        val iter = FqlParser.runQuery(q, null, conn)!!
        //val nextVal = iter.next()
        val strRes = ri.test.util.dump(iter)
        Assert.assertEquals(strRes, expect)
        conn.close()
    }

    Test fun lookup() {
        //run("[{'a':'val1'},{'a':'val2'}]", "link 'other.json' by a as other from top select other['val2']", "[{f:'val2'}]")
        run("[{'a':'val1'},{'a':'val2'}]", "link 'jsondriver/testresources/other.json' by a.b as other from top select other[a] limit 1", "[{f:[{a:{b:'val1'},c:'result1'}]}]")
        run("[{'a':'val1','cmp':'result1'},{'a':'val2','cmp':'result2'}]", "link 'jsondriver/testresources/other.json' by a.b as other from top select other[a].c = cmp", "[{f1:true},{f2:true}]")
    }


}