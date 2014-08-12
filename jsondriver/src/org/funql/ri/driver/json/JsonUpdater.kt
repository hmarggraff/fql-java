package org.funql.ri.driver.json
/**
 * Created by hmf on 03.11.13.
 */

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.Dumper
import java.io.FileWriter
import org.funql.ri.data.FqlDataException
import org.funql.ri.data.NamedValues
import org.funql.ri.kotlinutil.KUpdater
import org.funql.ri.util.NamedValuesImpl

abstract class JsonUpdaterBase(val targetName: String, fieldNames: Array<out String>) : KUpdater(fieldNames) {
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


class JsonListUpdater(targetName: String, fieldNames: Array<out String>, val data: MutableList<in Any?>) : JsonUpdaterBase(targetName, fieldNames) {

    override fun kput(values: Array<out Any?>): NamedValues {
        data.add(buildMap(values))
        return NamedValuesImpl(array<String>("it"), array<Any>(data.size - 1))
    }


    override fun kput(values: Array<out Any?>, key: Any) {
        data.add(key as Int, buildMap(values))
    }


    override fun getData(): Any = data
}
class JsonMapUpdater(targetName: String, fieldNames: Array<out String>, val data: MutableMap<in Any, in Any?>) : JsonUpdaterBase(targetName,fieldNames) {

    override fun getData(): Any = data

    override fun kput(values: Array<out Any?>): NamedValues {
        throw FqlDataException("Map must be updated with a key")
    }


    override fun kput(values: Array<out Any?>, key: Any) {
        data.put(key, buildMap(values))
    }
}
