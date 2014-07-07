package org.fqlsource.fqltest.testguitest

/**
 * Created by hans_m on 30.06.2014.
 */

import org.testng.annotations.Test;
import org.funql.ri.gui.RunnerView
import org.funql.ri.gui.RunnerControl
import org.mockito.Mockito.*;
import java.io.File
import java.io.FileOutputStream
import org.funql.ri.kotlinutil.NamedStringPair
import kotlin.test.assertEquals
import gui.DriverInfo
import org.yaml.snakeyaml.Yaml

class DriverListTest {

    Test fun testControlClear(){
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        t.clear()
        val testRunnerView: RunnerView? = verify(view)
        testRunnerView!!.setQueryText("")
    }
    Test fun testDriverLoadEmpty(){
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        val drivers = t.getJdbcDrivers()
        assert(drivers.size == 0)
    }

    Test fun testDriverLoadBasic(){
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        val drivers = t.getJdbcDrivers()
        assert(drivers.size == 1)
    }
    Test fun testSaveDrivers(){
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        t.putDriver("1", "2", "3")
        t.putDriver("a", "b", "c")

        t.saveJdbcDrivers()
        val drivers = t.getJdbcDrivers()
        assert(drivers.size == 2)
        assert("1".equals(drivers[0].getName()))
        assert("a".equals(drivers[1].getName()))
        assert("b".equals(drivers[1].className))
    }

    Test fun testAddDriver(){
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        t.putDriver("a1", "b2", "c3")
        val drivers = t.getJdbcDrivers()
        val found = drivers.first() {
            "a1".equals(it.getName())
        }
        assertEquals("b2", found.className)
        assertEquals("c3",found.jar)
    }
   Test fun testYaml(){
       val drivers = array(DriverInfo("a1", "b2", "c3"), DriverInfo("a", "b", "c"))
       val y = Yaml()
       val s = y.dump(drivers[0].asMap())
       assertEquals("{jar: c3, name: a1, class: b2}\n", s)
    }

}