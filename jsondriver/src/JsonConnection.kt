package org.funql.ri.jsondriver

import java.util.ArrayList
import org.funql.ri.data.FunqlConnectionWithRange
import org.funql.ri.data.FqlDataException
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.yaml.snakeyaml.Yaml
import org.funql.ri.util.ListFqlIterator
import java.io.File
import java.io.FileInputStream
import org.funql.ri.kotlinutil.KNamedImpl
import org.funql.ri.exec.Updater

public open class JsonConnection(name: String, propsArg: Map<String, String>?) : KFunqlConnection(name)
{
    val props: Map<String, String> = propsArg!!

    fun open(fileName: String): Any? {
        val readText = props.get(fileName)?: File(fileName).readText()
        val data: Any? = if (props.get("allYamlParts") == "true") Yaml().loadAll(readText) else Yaml().load(readText)
        return data
    }


    override fun kgetIterator(streamName: String): FqlIterator {
        val json: Any? = open(streamName)
        if (json is Map<*, *>) return JsonArrayIterator(streamName, arrayListOf(json))
        else if (json is ArrayList<*>) return JsonArrayIterator(streamName, json as ArrayList<*>)
        else throw FqlDataException("Entry point is not a list or a map: " + streamName)
    }

    override fun krange(fileName: String, startKey: String, endKey: String, includeEnd: Boolean): FqlIterator
    {
        val data = open(fileName)
        if (data is List<*>)
        {
            val start = startKey.toInt()
            val end = endKey.toInt()
            val subList = (data as List<*>).subList(start, end)
            return ListFqlIterator(subList)
        }
        else
            throw FqlDataException("Connection '$fileName' is not a range.")
    }

    override fun kuseMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer? {
        val otherData = getMappingData(name)
        if (otherData is List<*>){
            return JsonListLookup(name, fieldpath, otherData, single)
        }
        else if (otherData is Map<*, *>) {
            if (fieldpath.size() != 1)
                throw FqlDataException("The key for a Json map must be a single string, not a path: " + fieldpath.makeString("."))
            else
                return JsonMapAccess(fieldpath[0], otherData)
        }

        else
        throw FqlDataException("Json data '" + getName() + "' is not a map or list.")
    }

    override fun kgetUpdater(targetName: String): Updater? {
        val f = File(targetName)
        if (f.exists()){
            val parsedJson = open(targetName)
            if (parsedJson is MutableList<*>)
                return JsonListUpdater(targetName, parsedJson as MutableList<in Any?>)
            else return JsonMapUpdater(targetName, parsedJson as MutableMap<in Any, in Any?>)
        }
        else
            return JsonListUpdater(targetName, ArrayList())
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

    override fun kgetMember(from: Any?, member: String): Any? = if (from is Map<*, *>) from.get(member) else null
}

