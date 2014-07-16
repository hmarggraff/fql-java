package org.funql.ri.gui.prefs

import java.util.prefs.Preferences
import java.util.ArrayList
import java.util.HashMap
import org.funql.ri.gui.Factory
import org.funql.ri.util.Keys

val connectionsPath = Factory.prefKey+"/connections"
val driverPath = Factory.prefKey+"/drivers"

fun getConnections(): List<MutableMap<String, String>> {
    val anchor = Preferences.userRoot()?.node(connectionsPath)!!
    val ret = ArrayList<MutableMap<String, String>>()
    for (cnt in 1..10)
    {
        val nodename = cnt.toString()
        if (!anchor.nodeExists(nodename)) break
        val node = anchor.node(nodename)!!
        val keys: Array<String>? = node.keys()
        if (keys == null) break;
        val m = HashMap<String, String>()
        ret.add(m)
        for (k in keys)
        {
            val value: String? = node.get(k, null)
            if (value != null) m.put(k, value)
        }
    }
    return ret;
}

fun getDrivers(): List<MutableMap<String, String>> {
    val anchor = Preferences.userRoot()?.node(driverPath)!!
    val ret = ArrayList<MutableMap<String, String>>()
    val drivers: Array<String>? = anchor.keys()
    if (drivers == null)
        return ret;
    for (d in drivers)
    {
        val node = anchor.node(d)!!
        val keys: Array<String>? = node.keys()
        if (keys == null) continue;
        val m = HashMap<String, String>()
        ret.add(m)
        for (k in keys)
        {
            val value: String? = node.get(k, null)
            if (value != null) m.put(k, value)
        }
    }
    return ret;
}

fun saveDrivers(drivers:List<MutableMap<String, String>>) {
    var root = Preferences.userRoot()!!
    val newanchor = recreateNode(root, driverPath)
    for (d in drivers)
    {
        val driver = d[Keys.driver]!!
        val driverNode = newanchor.node(driver)!!
        for (e in d)
        {
            driverNode.put(e.key, e.value)
        }
    }
    newanchor.flush()
}

private fun recreateNode(parent: Preferences, name:String): Preferences {
    if (parent.nodeExists(name)) {
        val anchor: Preferences? = parent.node(driverPath)
        anchor?.removeNode()
    }
    return parent.node(connectionsPath)!!

}


public fun saveConnection(props: Map<String, String>) {
    val name = props["conName"]
    val anchor = Preferences.userRoot()?.node(connectionsPath)!!
    val ret = ArrayList<List<Pair<String, String>>>()
    var at = -1
    var removeAt = 10
    for (cnt in 1..10)
    {
        val nodename = cnt.toString()
        if (!anchor.nodeExists(nodename)) break
        val node = anchor.node(nodename)!!
        val keys: Array<String>? = node.keys()
        if (keys == null) break;
        val m = ArrayList<Pair<String, String>>()
        at++;
        for (k in keys)
        {
            val value: String? = node.get(k, null)
            if (value != null){
                if (k.equals("conName") && value.equals(name)) removeAt = at
                m + Pair<String, String>(k, value)
            }
        }
        ret + m
    }
    anchor.removeNode()
    val newanchor = Preferences.userRoot()?.node(connectionsPath)!!
    if (removeAt < ret.size -1)
        ret.remove(removeAt)
    else if (ret.size > 9)
        ret.remove(ret.size-1)
    val listToSave = props.map { Pair<String, String>(it.getKey(), it.getValue()) }
    ret.add(0, listToSave)
    for (cnt in 1..ret.size)
    {
        val nodename = cnt.toString()
        val node = newanchor.node(nodename)!!
        for (k : Pair<String, String> in ret[cnt-1]) node.put(k.first, k.second)
        node.flush()
    }
    newanchor.flush()
}

fun getQueryText(): String? = Preferences.userRoot()?.node(Factory.prefKey)?.get("querytext",null)
fun setQueryText(querytext: String?) {
    if (querytext != null)
        Preferences.userRoot()?.node(Factory.prefKey)?.put("querytext",querytext)
}


