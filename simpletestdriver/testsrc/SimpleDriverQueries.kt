package org.funql.ri.coretest

import kotlin.test.assertEquals
import org.funql.ri.parser.FqlParser
import org.testng.annotations.Test

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

    Test(enabled=false) fun BasicUseAndFromClauses() {
        run("from e1 select x", "[{x:'1.x'}]")
        run("from e1 select e1", "[{e1:'1.e1'}]")
        run("from e2 select a", "[{a:'1.a'},{a:'2.a'}]")
        run("from e2 select a,b", "[{a:'1.a',b:'1.b'},{a:'2.a',b:'2.b'}]")
        run("from e2", "[1,2]")
    }

    // select @other.field[herefield]

    Test fun RefClauses() {
        run("link x from e1 select x[yes]", "from x where primary=true")
        run("link x from e1 select x[a]", "from x where primary=1.a")
        run("link x from e2 select x[bla]", "[from x where primary=1.bla, from x where primary=2.bla]")
    }

    Test(enabled=false) fun Expressions() {
        run("from e1 select 3+5, 3.0 + 1, 3.0 = 3", "[8, 4.0, true]")
        run("from e1 select true and false", "false")
        run("from e1 select true and true", "true")
        run("from e1 select not true", "false")
        run("from e1 select  3> 5 = false", "true")
        run("from e1 select  3> 5 = false = true", "true")
        run("from e1 select  3> 5 = 5<3", "true")
        run("from e3 where true ", "[1, 2, 3]")
    }

    Test(enabled=false) fun bla() = run("from e3 where false ", "[]")

    Test(enabled = false)
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
