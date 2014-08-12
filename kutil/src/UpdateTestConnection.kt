package org.funql.ri.kotlinutil

/**
 * Created by hmf on 10.11.13.
 */


import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.exec.Updater
import java.util.HashMap
import java.util.LinkedHashMap
import org.funql.ri.data.NamedValues
import org.funql.ri.util.NamedValuesImpl

/**
 * a connection, that returns updaters for testing
 */
class UpdateTestConnection(name:String):KFunqlConnection(name){
    val updaters = HashMap<String, KTestUpdater>()

    override fun getIterator(streamName: String): FqlIterator {
        throw UnsupportedOperationException()
    }

    override fun useMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer? {
        throw UnsupportedOperationException()
    }
    override fun getMember(from: Any, member: String): Any? {
        throw UnsupportedOperationException()
    }


    override fun getUpdater(targetName: String, fieldNames: Array<out String>): Updater {
        val ret = KTestUpdater(targetName, fieldNames)
        updaters.put(targetName, ret)
        return ret
    }


    override fun close() {
    }
}

/**
 * an updater that updates a linkedHashMap for later examination in tests
 */
class KTestUpdater(val name: String, fieldNames: Array<out String>):KUpdater(fieldNames){
    public val data:LinkedHashMap<Any, Any?> = LinkedHashMap()

    override fun kput(values: Array<out Any?>): NamedValues {
        data.put(data.size(), buildMap(values))
        return NamedValuesImpl("id", data.size()-1)
    }
    override fun kput(values: Array<out Any?>, key: Any) {
        data.put(key, buildMap(values))
    }
    override fun commit() {
    }
}
