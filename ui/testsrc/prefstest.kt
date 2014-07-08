package org.fqlsource.fqltest.testguitest

import org.testng.annotations.Test
import org.testng.annotations.BeforeSuite
import org.funql.ri.gui.RunMode
import java.util.prefs.Preferences
import org.funql.ri.gui.Factory
import org.funql.ri.util.Keys

class Prefs()
{
    BeforeSuite fun setFactory() = Factory.testMode()

    Test fun save() {
        val preferences = Preferences.userRoot()!!.node(Factory.prefKey+"/connections")!!
        preferences.removeNode()
        preferences.flush()
        val p = hashMapOf(Keys.connection to "a", Keys.driver to "json", Keys.text to "{a:a}")
        org.funql.ri.gui.prefs.saveConnection(p)
        val list = org.funql.ri.gui.prefs.getConnections()
        assert(list.size == 1)

        assert(list[0][Keys.connection] == "a")
    }
}
