package org.funql.ri.coretest

import java.util.ArrayList
import kotlin.test.assertEquals
import org.fqlsource.fqltest.simpletestdriver.SimpleTestConnection
import org.funql.ri.parser.FqlParser
import org.yaml.snakeyaml.Yaml

public open class SimpleTestDriverBase {

    val conn = callInit()
    val yaml = Yaml()


    fun callInit(): SimpleTestConnection {
        val p = hashMap<String?, String?>();
        val tconn = SimpleTestConnection("SimpleTest", p);
        return tconn
    }

    fun run(query: String, expectation: Any) {
        print(query)
        print(" --> ")
        val  it = FqlParser.runQuery(query, null, conn)!!
        val result: MutableList<Any?> = ArrayList<Any?>()      // Annotation required for a bug in kotlin v 0.4.68
        while (it.hasNext()) {
            val nexta = it.next()
            if (nexta is Array<Any> && nexta.size == 1)
                result.add(nexta[0]);
            else
                result.add(nexta)
        }
        val dump = if (result.size() == 1) yaml.dump(result[0])!! else yaml.dump(result)!!
        val shortRes = dump.substring(0, dump.length() - 1);
        println(shortRes);
        assertEquals(expectation, shortRes)
    }
}
