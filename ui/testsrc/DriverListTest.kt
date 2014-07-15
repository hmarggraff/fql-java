package org.fqlsource.fqltest.testguitest

/**
 * Created by hans_m on 30.06.2014.
 */

import org.testng.annotations.Test;
import org.funql.ri.gui.RunnerView
import org.funql.ri.gui.RunnerControl
import org.mockito.Mockito.*;
import java.io.File
import kotlin.test.assertEquals
import org.yaml.snakeyaml.Yaml
import org.funql.ri.util.Keys
import org.testng.annotations.BeforeSuite
import org.funql.ri.gui.Factory
import java.math.BigDecimal
import org.apache.logging.log4j.LogManager
import java.util.Date

val log = LogManager.getLogger("drivers.jdbc")!!

class DriverListTest {

    BeforeSuite fun setTestMode() {
        Factory.testMode()
        log.info("Start: ${Date()}")
        log.info("Runmode= ${Factory.mode}")
    }


    Test fun testYaml() {
        log info "testYaml"
        val drivers = array(sortedMapOf(Keys.driver to "1", Keys.klass to "2", Keys.file to "3"))
        val y = Yaml()
        val s = y.dump(drivers)
        assertEquals("- {class: '2', driver: '1', file: '3'}\n", s)
    }

    /**
     * tests if clearing the controller properly calls the view
     */
    Test fun testControlClear() {
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        t.clear()
        val testRunnerView: RunnerView? = verify(view)
        testRunnerView!!.setQueryText("")
    }


    Test fun testDriverLoadEmpty() {
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val file = File("driversTest.yaml")
        if (file.exists()) file.delete()
        val t = RunnerControl(view)
        val drivers = t.getJdbcDrivers()
        assert(drivers.size == 1)
        assert("drivername".equals(drivers[0][Keys.driver]))
        assert("driverclass".equals(drivers[0][Keys.klass]))
        assert("c:\\bla.jar".equals(drivers[0][Keys.file]))

    }

    Test fun testDriverLoadBasic() {
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        t.extractDriversFromResourcesToFile()
        val drivers = t.getJdbcDrivers()
        assert(drivers.size == 1)
    }


    //control.driverKey, control.driverClassKey, control.fileKey
    Test fun testSaveDrivers() {
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        t.putDriver(hashMapOf(Keys.driver to "a", Keys.klass to "b", Keys.file to "c"))
        t.putDriver(hashMapOf(Keys.driver to "1", Keys.klass to "2", Keys.file to "3"))

        t.saveJdbcDrivers()
        val drivers: Array<Map<String, String>> = t.getJdbcDrivers()
        assert(drivers.size == 2)
        assert("1".equals(drivers[0][Keys.driver]))
        assert("a".equals(drivers[1][Keys.driver]))
        assert("b".equals(drivers[1][Keys.klass]))
    }

    Test fun testAddDriver() {
        val view: RunnerView = mock(javaClass<RunnerView>())!!
        val t = RunnerControl(view)
        t.putDriver(hashMapOf(Keys.driver to "a1", Keys.klass to "b2", Keys.file to "c3"))
        val drivers = t.getJdbcDrivers()
        val found = drivers.first() {
            "a1".equals(it[Keys.driver])
        }
        assertEquals("b2", found[Keys.klass])
        assertEquals("c3", found[Keys.file])
    }
}