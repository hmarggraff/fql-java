/**
 * Created by hans_m on 22.07.2014.
 */

import org.testng.annotations.Test
import org.funql.ri.util.SkipTest
import org.funql.ri.parser.FqlParser
import org.testng.Assert
import java.util.ArrayList
import org.funql.ri.simpletestdriver.SimpleTestConnection
import java.util.HashMap
import org.funql.ri.exec.FqlStatement

class TestStatementVisitor()
{
    val conn = SimpleTestConnection("SimpleTest", HashMap<String, String>());


    Test fun relQuery(){
        val clauses = run("from Organisation")!!
        assert(clauses.size == 1)

    }
    Test fun relQuerySel(){
        val clauses: MutableList<FqlStatement> = run("from Organisation join Person on Organisation.id = Person.organisation_id select city")!!
        val s = SqlMapper(clauses).toSql()
        println(s)
        assert(clauses.size == 3)
    }

    fun run(q: String) = FqlParser(q,conn).parseClauses()
}
