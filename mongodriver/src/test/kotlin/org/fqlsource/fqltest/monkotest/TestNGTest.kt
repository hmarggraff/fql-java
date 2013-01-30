package org.fqlsource.fqltest.monkotest

import org.testng.annotations.Test
import org.testng.annotations.DataProvider
import kotlin.test.assertEquals

var cnt: Int = 0

class TestNGTest()
{
    val init = callInit()
    var res : String = "";

    fun callInit():Int  { cnt++; println("init " + cnt); return cnt}

    DataProvider(name = "whattosay")
    fun createData1()  = array(array("gidday", "gidday"), array("true", "true"))


    Test(dataProvider = "whattosay") fun hello(say: String, say2: String) {

        println(say + " " + init)
        res = say
        assertEquals(say, say2)
    }

    Test(dependsOnMethods= array("hello"), alwaysRun=true) fun dodo() {
        println("res= $res")
        assertEquals(res, "gidday")
        //assert(false, "Why fail")
        //throw AssertionError("fail")
    }
}
