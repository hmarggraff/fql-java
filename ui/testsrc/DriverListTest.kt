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
import org.yaml.snakeyaml.Yaml
import org.funql.ri.util.SkipTest
import org.funql.ri.gui.GuiKeys

class DriverListTest {

    Test fun testYaml(){
        val drivers = array(hashMapOf(GuiKeys.driverKey to "1", GuiKeys.driverClassKey to "2", GuiKeys.fileKey to "3"))
        val y = Yaml()
        val s = y.dump(drivers)
        assertEquals("- {file: '3', driver: '1', driver_class: '2'}\n", s)
    }

    /**
     * tests if clearing the controller properly calls the view
     */
    Test fun testControlClear(){
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        t.clear()
        val testRunnerView: RunnerView? = verify(view)
        testRunnerView!!.setQueryText("")
    }


    SkipTest fun testDriverLoadEmpty(){
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        val drivers = t.getJdbcDrivers()
        assert(drivers.size == 0)
    }

    Test fun testDriverLoadBasic(){
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        t.extractDriversFromResourcesToFile()
        val drivers = t.getJdbcDrivers()
        assert(drivers.size == 1)
    }


    //control.driverKey, control.driverClassKey, control.fileKey
    Test fun testSaveDrivers(){
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        t.putDriver(hashMapOf(GuiKeys.driverKey to "1", GuiKeys.driverClassKey to "2", GuiKeys.fileKey to "3"))
        t.putDriver(hashMapOf(GuiKeys.driverKey to "a", GuiKeys.driverClassKey to "b", GuiKeys.fileKey to "c"))

        t.saveJdbcDrivers()
        val drivers: Array<Map<String, String>> = t.getJdbcDrivers()
        assert(drivers.size == 3)
        assert("1".equals(drivers[0][GuiKeys.driverKey]))
        assert("a".equals(drivers[1][GuiKeys.driverKey]))
        assert("b".equals(drivers[1][GuiKeys.driverClassKey]))
    }

    Test fun testAddDriver(){
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        t.putDriver(hashMapOf(GuiKeys.driverKey to "a1", GuiKeys.driverClassKey to "b2", GuiKeys.fileKey to "c3"))
        val drivers = t.getJdbcDrivers()
        val found = drivers.first() {
            "a1".equals(it[GuiKeys.driverKey])
        }
        assertEquals("b2", found[GuiKeys.driverClassKey])
        assertEquals("c3",found[GuiKeys.fileKey])
    }

}