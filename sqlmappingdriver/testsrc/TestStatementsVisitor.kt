/**
 * Created by hans_m on 22.07.2014.
 */

import org.testng.annotations.Test
import org.funql.ri.util.SkipTest
import org.funql.ri.parser.FqlParser
import org.testng.Assert
import java.util.ArrayList
import java.util.HashMap
import org.funql.ri.exec.FqlStatement
import org.funql.ri.simpletestdriver.SimpleTestConnection
import org.funql.ri.exec.ContainerSlot
import org.funql.ri.kotlinutil.KFunqlConnection
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.exec.Updater
import org.funql.ri.sqldriver.mapping.SqlMapper
import org.funql.ri.sqldriver.mapping.SqlMapper

class TestStatementVisitor()
{
    val conn = object:KFunqlConnection("SimpleTest") {

        override fun useMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer? {
            throw UnsupportedOperationException()
        }
        override fun getMember(from: Any, member: String): Any? {
            throw UnsupportedOperationException()
        }
        override fun getUpdater(targetName: String, fieldNames: Array<out String>): Updater {
            throw UnsupportedOperationException()
        }
        override fun getIterator(streamName: String): FqlIterator {
            throw UnsupportedOperationException()
        }

        override fun close() = throw UnsupportedOperationException()
    };


    Test fun relQuery(){
        val clauses = run("from Organisation")
        assert(clauses.size == 1)

    }
    Test fun relQuerySel(){
        val clauses: MutableList<FqlStatement> = run("from Organisation join Person on Organisation.id = Person.organisation_id select city")
        val ep = ContainerSlot("test", 1, "test", 1)
        val sqlMapper = SqlMapper(ep, "test", clauses)
        val s = sqlMapper.toSql()
        //sqlMapper.execute()

        println(s)
        assert(clauses.size == 3)
    }

    fun run(q: String) = FqlParser(q,conn).parseClauses()
}
