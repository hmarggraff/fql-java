package org.funql.ri.driver.json.test

import org.testng.annotations.Test
import org.funql.ri

class JsonQueryConnectTest
{
    fun run(q: String, expect: String)
    {
        val iter = FqlParser.runQuery(q)!!
        val nextVal = iter.next()
        val strRes = ri.test.util.dump(nextVal)
        Assert.assertEquals(strRes, expect)
    }

    Test
            fun testEmpty() {
        run("open{driver:\"org.funql.ri.driver.json.JsonDriver\","
        + "text:\"[{key: 25, val: 99}, {key: 26, val: 104}]\"} "
        + "from top where key > 25 select val",
                " {val:104}")
    }
}