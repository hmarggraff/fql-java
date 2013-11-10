/**
 * Created by hmf on 10.11.13.
 */

import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.exec.Updater
import java.util.HashMap
import java.util.LinkedHashMap

/**
 * a connection, that returns updaters for testing
 */
class UpdateTestConnection(name:String):KFunqlConnection(name){
    val updaters = HashMap<String, KTestUpdater>()

    override fun kgetIterator(streamName: String): FqlIterator {
        throw UnsupportedOperationException()
    }

    override fun krange(name: String, startKey: String, endKey: String, includeEnd: Boolean): FqlIterator {
        throw UnsupportedOperationException()
    }
    override fun kuseMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer? {
        throw UnsupportedOperationException()
    }
    override fun kgetMember(from: Any?, member: String): Any? {
        throw UnsupportedOperationException()
    }
    override fun kgetUpdater(targetName: String): Updater? {
        val ret = KTestUpdater(targetName)
        updaters.put(targetName, ret)
        return ret
    }

    override fun close() {
    }
}

/**
 * an updater that updates a linkedHashMap for later examination in tests
 */
class KTestUpdater(val name: String):KUpdater(){
    public val values:LinkedHashMap<Any, Any?> = LinkedHashMap<Any, Any?>()

    override fun kput(fieldNames: Array<out String>, value: Array<out Any?>): Any {
        values.put(values.size, buildMap(fieldNames,value))
        return values.size-1
    }
    override fun kput(fieldNames: Array<out String>, value: Array<out Any?>, key: Any) {
        values.put(key, buildMap(fieldNames,value))
    }
    override fun commit() {
    }
}
