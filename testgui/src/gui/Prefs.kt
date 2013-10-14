package org.funql.ri.gui.prefs

import java.util.prefs.Preferences
import java.util.ArrayList
import java.util.HashMap
import org.funql.ri.gui.Factory

val connectionsPath = Factory.prefKey+"/connections"
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


