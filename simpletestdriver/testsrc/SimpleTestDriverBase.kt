package org.funql.ri.coretest

import java.util.ArrayList
import kotlin.test.assertEquals
import org.fqlsource.fqltest.simpletestdriver.SimpleTestConnection
import org.funql.ri.parser.FqlParser
import org.yaml.snakeyaml.Yaml
import java.util.HashMap
import org.funql.ri.data.FqlIterator

public open class SimpleTestDriverBase {

    val conn = callInit()
    val yaml = Yaml()


    fun callInit():  ArrayList<SimpleTestConnection> {
        val p = HashMap<String, String>();
        val tconn = SimpleTestConnection("SimpleTest", p);
        val ret =  ArrayList<SimpleTestConnection>()
        ret.add(tconn)
        return ret
    }

    fun run(query: String, expectation: Any) {
        print(query)
        println(" -->")
        val  it = FqlParser.runQuery(query, null, conn)!!
        val result: MutableList<Any?> = ArrayList<Any?>()      // Annotation required for a bug in kotlin v 0.4.68
        while(true)
            {
                val t: Any? = it.next()
                if (t  == FqlIterator.sentinel) break
                if (t is Array<Any> && t.size == 1) result.add(t[0]);
                else result.add(t)
            }
        val dump = if (result.size() == 1) yaml.dump(result[0])!! else yaml.dump(result)!!
        val shortRes = dump.substring(0, dump.length() - 1);
        println(shortRes);
        assertEquals(expectation, shortRes)
    }
}
