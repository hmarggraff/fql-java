package org.funql.ri.coretest

import java.util.ArrayList
import kotlin.test.assertEquals
import org.fqlsource.fqltest.simpletestdriver.SimpleTestConnection
import org.funql.ri.parser.FqlParser
import org.yaml.snakeyaml.Yaml
import java.util.HashMap
import org.funql.ri.data.FqlIterator
import org.funql.ri.test.util.dump

public open class SimpleTestDriverBase {

    val conn = callInit()

    fun callInit():  ArrayList<SimpleTestConnection> {
        val p = HashMap<String, String>();
        val tconn = SimpleTestConnection("SimpleTest", p);
        val ret =  ArrayList<SimpleTestConnection>()
        ret.add(tconn)
        return ret
    }

    /**
     * skip a run
     */

    fun skiprun(query: String, expectation: Any) {
        // do nothing
    }
    fun run(query: String, expectation: Any) {
        print(query)
        print(" --> ")
        val  it = FqlParser.runQuery(query, null, conn)!!
        val result: MutableList<Any?> = ArrayList<Any?>()      // Annotation required for a bug in kotlin v 0.4.68
        while(true)
            {
                val t: Any? = it.next()
                if (t  == FqlIterator.sentinel) break
                if (t is Array<Any> && t.size == 1) result.add(t[0]);
                else result.add(t)
            }
        val shortRes = dump(result)
        println(shortRes);
        assertEquals(expectation, shortRes)
    }
}
