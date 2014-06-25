/**
 * Created by hmf on 10.11.13.
 */
package org.funql.ri.kotlinutil
import org.funql.ri.exec.Updater
import java.util.HashMap
import org.funql.ri.data.NamedValues

public abstract class KUpdater(protected val fieldNames: Array<out String>) : Updater{
    override fun put(values: Array<out Any>?): NamedValues = kput(values!!)
    override fun put(value: Array<out Any>?, key: Any?): Unit = kput(value!!, key!!)
    abstract fun kput(values: Array<out Any?>): NamedValues
    abstract fun kput(values: Array<out Any?>, key: Any): Unit
    protected fun buildMap(value: Array<out Any?>): HashMap<String, Any?> {
        val obj = HashMap<String, Any?>()
        for (i in (0..fieldNames.size - 1))
            obj.put(fieldNames[i], value[i])
        return obj

    }

}