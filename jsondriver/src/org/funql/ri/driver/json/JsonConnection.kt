package org.funql.ri.driver.json

import org.funql.ri.kotlinutil.KFunqlConnection
import org.funql.ri.data.FqlIterator
import java.util.ArrayList
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.exec.Updater
import java.io.File
import org.funql.ri.data.FqlDataException
import org.funql.ri.jsondriver
import org.funql.ri.jsondriver.JsonListUpdater
import org.funql.ri.jsondriver.JsonMapUpdater

public open class JsonConnection(name: String, propsArg: Map<String, String>?) : KFunqlConnection(name)
{
    val props: Map<String, String> = propsArg!!

    fun open(fileName: String): Any? {
        val file = File(fileName)
        //val path = file.getAbsolutePath()
        val readText = props.get(fileName) ?: file.readText()
        val data: Any? = if (props.get("allYamlParts") == "true") Yaml().loadAll(readText) else Yaml().load(readText)
        return data
    }


    override fun getIterator(streamName: String): FqlIterator {
        val json: Any? = open(streamName)
        if (json is Map<*, *>) return JsonArrayIterator(streamName, arrayListOf(json))
        else if (json is ArrayList<*>) return JsonArrayIterator(streamName, json as ArrayList<*>)
        else throw FqlDataException("Entry point is not a list or a map: " + streamName)
    }

    override fun useMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer? {
        val otherData = getMappingData(name)
        if (otherData is List<*>){
            return jsondriver.JsonListLookup(name, fieldpath, otherData, single)
        }
        else if (otherData is Map<*, *>) {
            if (fieldpath.size() != 1)
                throw FqlDataException("The key for a Json map must be a single string, not a path: " + fieldpath.makeString("."))
            else
                return jsondriver.JsonMapAccess(fieldpath[0], otherData)
        }

        else
        throw FqlDataException("Json data '" + getName() + "' is not a map or list.")
    }

    override fun getUpdater(targetName: String, fieldNames: Array<out String>): Updater {
        val f = File(targetName)
        if (f.exists()){
            val parsedJson = open(targetName)
            if (parsedJson is MutableList<*>)
                return JsonListUpdater(targetName, fieldNames, parsedJson as MutableList<in Any?>)
            else return JsonMapUpdater(targetName, fieldNames, parsedJson as MutableMap<in Any, in Any?>)
        }
        else
            return JsonListUpdater(targetName, fieldNames, ArrayList())
    }


    private fun getMappingData(name: String): Any
    {
        val otherData = props.get(name)
        if (otherData != null) {
            val ret = Yaml().load(otherData)
            return ret!!
        }
        else
        {
            val f = File(name)
            if (!f.exists()) throw FqlDataException("Neither a connection property named: " + name + " nor a file at: " + f.getAbsolutePath())
            val y = Yaml().load(FileInputStream(f))
            return y!!
        }
    }

    public override fun close()
    {
        //
    }

    override fun getMember(from: Any, member: String): Any? = if (from is Map<*, *>) from[member] else null
}