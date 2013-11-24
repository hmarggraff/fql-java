/**
 * Created by hmf on 10.11.13.
 */

import org.funql.ri.exec.Updater
import java.util.HashMap
import java.util.UUID
import org.funql.ri.data.NamedValues

public abstract class KUpdater : Updater{

    override fun put(fieldNames: Array<out String>?, value: Array<out Any>?): NamedValues = kput(fieldNames!!, value!!)
    override fun put(fieldNames: Array<out String>?, value: Array<out Any>?, key: Any?): Unit = kput(fieldNames!!, value!!, key!!)
    abstract fun kput(fieldNames: Array<out String>, value: Array<out Any?>): NamedValues
    abstract fun kput(fieldNames: Array<out String>, value: Array<out Any?>, key: Any): Unit
    protected fun buildMap(fieldNames: Array<out String>, value: Array<out Any?>): HashMap<String, Any?> {
        val obj = HashMap<String, Any?>()
        for (i in (0..fieldNames.size - 1))
            obj.put(fieldNames[i], value[i])
        return obj

    }

}