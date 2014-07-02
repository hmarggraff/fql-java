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
        val driversUrl = javaClass.getResource("/drivers.yaml")
        val readBytes = driversUrl!!.readBytes()
        val file = File("drivers.yaml").writeBytes(readBytes)
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        val drivers = t.getJdbcDrivers()
        assert(drivers.size == 1)
    }
    Test fun testSaveDrivers(){
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val td = array<NamedStringPair>(NamedStringPair("a", "b", "c"), NamedStringPair("1", "2", "3"))
        val t = RunnerControl(view)
        t.saveJdbcDrivers()
        val drivers = t.getJdbcDrivers()
        assert(drivers.size == 2)
        assert("1".equals(drivers[0].getName()))
        assert("a".equals(drivers[1].getName()))
        assert("b".equals(drivers[1].value))
    }

    Test fun testAddDriver(){
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        t.putDriver("a1", "b2", "c3")
        val drivers = t.getJdbcDrivers()
        val found = drivers.first() {
            "a1".equals(it.getName())
        }
        assertEquals("b2", found.value)
        assertEquals("c3",found.value2)
    }

}