package org.fqlsource.fqltest.testguitest

import org.testng.annotations.Test
import org.testng.annotations.BeforeSuite
import org.funql.ri.gui.RunMode
import java.util.prefs.Preferences
import org.funql.ri.gui.Factory
import org.funql.ri.gui.Keys
import org.funql.ri.gui.GuiKeys

class Prefs()
{
    BeforeSuite fun setFactory() {
        org.funql.ri.gui.mode = RunMode.test
    }
    Test fun save() {
        val preferences = Preferences.userRoot()!!.node(Factory.prefKey+"/connections")!!
        preferences.removeNode()
        preferences.flush()
        val p = hashMapOf(GuiKeys.connKey to "a", GuiKeys.driverKey to "json", GuiKeys.textKey to "{a:a}")
        org.funql.ri.gui.prefs.saveConnection(p)
        val list = org.funql.ri.gui.prefs.getConnections()
        assert(list.size == 1)

        assert(list[0][GuiKeys.connKey] == "a")
    }
}
