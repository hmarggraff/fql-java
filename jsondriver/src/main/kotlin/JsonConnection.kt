package org.funql.ri.jsondriver

import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.ArrayList
import java.util.LinkedHashMap
import org.fqlsource.ri.util.ListFqlIterator
import org.funql.ri.data.FunqlConnectionWithRange
import org.funql.ri.data.FqlDataException
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.util.Named
import org.funql.ri.util.SomethingMissingError
import org.yaml.snakeyaml.Yaml
import org.funql.ri.data.FqlMultiMapContainer


public open class JsonConnection(name: String, propsArg: Map<String, String>?): KNamedImpl(name), FunqlConnectionWithRange
{
    val props: Map<String, String> = propsArg!!

    fun open(): InputStream {
        if (props containsKey "file")
        {
            val file: String = props.get("file")!!
            try
            {
                return FileInputStream(file)
            }
            catch (ex: FileNotFoundException) {
                throw SomethingMissingError("Yaml driver did not find the input file speicified in the connection properties: " + file)
            }
        }
        else if (props containsKey "text")
        {
            return ByteArrayInputStream(props.get("text")!!.getBytes())
        }
        else
            throw SomethingMissingError("Yaml driver needs a file (name) to read from.")
    }


    val input = open()

    val data: Any? = if (props.get("allYamlParts") == "true") Yaml().loadAll(input) else Yaml().load(input)



    public override fun useIterator(streamName: String?): FqlIterator? {
        if (streamName != "top") throw FqlDataException("Entry point for list must be named top not: " + streamName)
        else if (!(data is ArrayList<*>)) throw FqlDataException("Entry point for list is not a list: " + streamName)
        return JsonArrayIterator(streamName, data as ArrayList<*>)
    }

    public override fun range(name: String?, startKey: String?, endKey: String?, includeEnd: Boolean): FqlIterator?  = range1(name!!, startKey!!, endKey!!, includeEnd)
    fun range1(name: String, startKey: String, endKey: String, includeEnd: Boolean): FqlIterator
    {
        if (data is List<*>)
        {
            val start = startKey.toInt()
            val end = endKey.toInt()
            val subList = (data as List<*>).subList(start, end)
            return ListFqlIterator(subList, name)
        }
        else if (data is LinkedHashMap<*, *>)
        {
            var inRange = false
            val iter = data.entrySet().iterator()
            val ret = ArrayList<Any>()
            while (iter.hasNext())
            {
                val elem = iter.next()
                val key = elem.getKey()
                val value = elem.getValue()
                if (key is String)
                    if (inRange)
                    {
                        if (endKey == key)
                        {
                            inRange = false
                            if (includeEnd)
                                ret.add(value as String)
                        }
                    }
                    else
                    {
                        if (startKey == key)
                        {
                            inRange = true
                            ret.add(value as String)
                        }
                    }
            }
            return ListFqlIterator(ret, name)
        }
        else
            throw FqlDataException("Connection '" + getName() + "' is not a range.")
    }

    public override fun useMap(fieldpath: List<String>?): FqlMapContainer? =
            if (fieldpath == null) throw NullPointerException("field path $fieldpath may not be null.")
            else if (data is Map<*, *>) JsonMapAccess(fieldpath[0], data)
            else throw FqlDataException("Connection '" + getName() + "' is not a map.")


    public override fun useMultiMap(fieldpath: List<String>?): FqlMultiMapContainer? =
            if (fieldpath == null) throw NullPointerException("field path $fieldpath may not be null.")
            else if (data is Map<*, *>) JsonMultiMapAccess(fieldpath.get(0), data)
            else throw FqlDataException("Connection '" + getName() + "' is not a map.")

    public override fun close()
    {
        input.close()
    }
    public override fun getMember(from: Any?, member: String?): Any?  = if (from is Map<*, *>) from.get(member) else null
}


class JsonMapAccess(name: String, val data: Map<*, *>): KNamedImpl(name), FqlMapContainer
{
    public override fun lookup(key: Any?): Any? = data.get(key)
}

class JsonMultiMapAccess(name: String, val data: Map<*, *>): KNamedImpl(name), FqlMultiMapContainer
{
    public override fun lookup(key: Any?): FqlIterator? {
        val ret = data[key]
        if (ret is List<*>)
            return JsonArrayIterator(getName(), ret)
        throw FqlDataException("Entry of multi-map " + getName() + " is not a list. but a " + ret.javaClass)
    }
}

