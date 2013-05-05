package org.funql.ri.coretest

import org.funql.ri.data.FqlDataException
import org.funql.ri.parser.FqlParseException
import org.testng.annotations.Test
import kotlin.test.fail

public class BadQueries: SimpleTestDriverBase() {
    Test fun RecognizeErroneousQueries()
    {
        array("use x from e1 select y[1]", "use x from e1 select x[]", "silly", "\"eofinstring", "from \"", "use \"ep2\"", "use b as from a ", "use b", "use b as \"bla\"",
                "open {}", "open { driver=\"bla\"}", "open from", "open { from=\"bla\"", "from e1 where !a", "use x from e1 select y(yes)")
                .forEach {
            try {
                run(it, "")
                fail("Parse exception not thrown in: " + it)
            }
            catch (x: FqlParseException){
                println(x.getMessage() + " <-- " + it)
            }
        }
    }


    Test fun CheckForRuntimeErrors()
    {
        array("from ep1").forEach{
            try {
                run(it, "")
                fail("Runtime exception not thrown in: " + it)

            }
            catch (x: FqlDataException){
                println(x.getMessage() + " <-- " + it)
            }
        }
    }
}