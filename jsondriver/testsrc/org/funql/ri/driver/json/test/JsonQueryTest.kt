package org.funql.ri.driver.json.test

import org.funql.ri
import org.testng.annotations.Test

class JsonQueryTest: ri.jsondriver.test.JsonTestBase()
{

    Test fun updater()
    {
        runQuery("[]", "into text put 'x'", FqlIterator.sentinel)

    }
    //Test
            fun testEmpty() {
        runQuery("[]", "from text", FqlIterator.sentinel)
    }
    Test fun oneObject() {
        runQuery("{a: b, c: d}", "from text select a", "{a:b}")
    }
    Test fun testApp2() { runQuery("{a: b, c: d}", "from text select a + c", "{f:bd}") }
    Test fun testAppFields() { runQuery("[2,3,5,7]", "from text where it > 5 select it", "{f:7}") }
    Test fun lookup() {
        runQuery("[1,6]", "link 'other.json' by a.b from text where it > 5 select it", "{f:7}") }
    //Test fun testMultiMap() { run("{a: [2,3,5,7]}", "from top select from a where it < 3 select it", 2) }


}