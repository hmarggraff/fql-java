/**
 * Created by hmf on 03.11.13.
 */

import org.funql.ri.exec.Updater
import java.util.ArrayList

class JsonFileUpdater(val targetName: String, val data: ArrayList<Any?>):Updater {


    override fun put(fieldNames: Array<out String>?, value: Array<out Any>?): Any? {
        data.add(value)
        return data.size-1
    }

     fun comitt(){

    }
}
