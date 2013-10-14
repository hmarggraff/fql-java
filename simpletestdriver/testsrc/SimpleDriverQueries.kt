package org.funql.ri.coretest

import kotlin.test.assertEquals
import org.funql.ri.parser.FqlParser
import org.testng.annotations.Test
import org.funql.ri.util.SkipTest

class SimpleDriverQueries: SimpleTestDriverBase()
{


    Test fun testBasicParsing() {
        val query = "from\n\t\r\ne0"
        val p = FqlParser(query, conn)

        val clauses = p.parseClauses()!!
        assertEquals(clauses.size(), 1)
        assertEquals(p.getQueryString(), query)
        assertEquals(p.getPos(), query.length())
    }

    Test fun BasicUseAndFromClauses() {
        skiprun("from e1 select e1", "[1_e1]")
        run("from e1 select e12", "[1_e12]")
        run("from e2 select a", "[1_a,2_a]")
        run("from e2 select a,b", "[{a:1_a,b:1_b},{a:2_a,b:2_b}]")
        run("from e2", "[1,2]")
    }

    Test fun RefClauses() {
        run("link x from e1 select x[yes]", "from x where primary=true")
        run("link x from e1 select x[a]", "from x where primary=1.a")
        run("link x from e2 select x[bla]", "[from x where primary=1.bla, from x where primary=2.bla]")
    }

    Test fun Expressions() {
        run("from e1 select 3+5, 3.0 + 1, 3.0 = 3", "[{f1:8,f2:4.0,f3:true}]")
        run("from e1 select true and false", "[false]")
        run("from e1 select true and true", "[true]")
        run("from e1 select true or false", "[true]")
        run("from e1 select not true", "[false]")
        run("from e1 select  3> 5 = false", "[true]")
        run("from e1 select  3> 5 = false = true", "[true]")
        run("from e1 select  3> 5 = 5<3", "[true]")
        run("from e3 where true ", "[1,2,3]")
    }

    Test fun bla() = run("from e3 where false ", "[]")

    SkipTest(enabled = false)
            fun elements() {
        run("from e3 as x where not x >= 2 select x", 1)
        run("from e2 where e2 > 1 select e2", "2")
        run("from e3 as x where x <\n3 select x", "[1, 2]")
        run("from e3 as x where x <= 2", "[1, 2]")
        run("from e3 as x where x >= 2", "[2, 3]")
        run("from e3 as x where x != 2", "[1, 3]")
        run("from e3 as x where x = 2", "2")
        run("from e3 as x select x+2", "[3, 4, 5]")
        run("from e3 as x select x-2", "[-1, 0, 1]")
        run("from e3 as x select x*x", "[1, 4, 9]")
    }


}
