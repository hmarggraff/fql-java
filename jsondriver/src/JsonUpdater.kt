/**
 * Created by hmf on 03.11.13.
 */

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.Dumper
import java.io.FileWriter
import org.funql.ri.data.FqlDataException
import org.funql.ri.data.NamedValues
import org.funql.ri.kotlinutil.NamedValuesKImpl

abstract class JsonUpdaterBase(val targetName: String) : KUpdater() {
    public override fun commit() {
        val y: Yaml = Yaml(Dumper())
        var fileWriter: FileWriter?
        try {
            fileWriter = FileWriter(targetName)
            y.dump(getData(), fileWriter)
        }finally {
        }
        fileWriter?.close()
    }

    abstract fun getData(): Any

}


class JsonListUpdater(targetName: String, val data: MutableList<in Any?>) : JsonUpdaterBase(targetName) {

    protected val names: Array<String> = array<String>("it")

    override fun kput(fieldNames: Array<out String>, value: Array<out Any?>): NamedValues {
        data.add(buildMap(fieldNames, value))
        return NamedValuesKImpl(names, array<Any?>(data.size - 1))
    }


    override fun kput(fieldNames: Array<out String>, value: Array<out Any?>, key: Any) {
        data.add(key as Int, buildMap(fieldNames, value))
    }


    override fun getData(): Any = data
}
class JsonMapUpdater(targetName: String, val data: MutableMap<in Any, in Any?>) : JsonUpdaterBase(targetName) {

    override fun getData(): Any = data

    override fun kput(fieldNames: Array<out String>, value: Array<out Any?>): NamedValues {
        throw FqlDataException("Map must be updated with a key")
    }


    override fun kput(fieldNames: Array<out String>, value: Array<out Any?>, key: Any) {
        data.put(key, buildMap(fieldNames, value))
    }
}
