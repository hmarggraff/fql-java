package org.funql.ri.coretest

import java.util.ArrayList
import kotlin.test.assertEquals
import org.fqlsource.fqltest.simpletestdriver.SimpleTestConnection
import org.funql.ri.parser.FqlParser
import java.util.HashMap
import org.funql.ri.data.FqlIterator
import org.funql.ri.exec.NamedValue
import org.funql.ri.exec.NamedLong
import org.funql.ri.exec.NamedDouble
import org.funql.ri.exec.NamedBoolean
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

    fun run(query: String, expectation: Any) {
        print(query)
        print(" --> ")
        val  it = FqlParser.runQuery(query, null, conn)!!
        val shortRes = dump(it)
        println(shortRes);
        assertEquals(expectation, shortRes)
    }
}
