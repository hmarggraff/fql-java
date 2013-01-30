package org.funql.ri.jsondriver

import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.ArrayList
import java.util.LinkedHashMap
import org.fqlsource.ri.util.ListFqlIterator
import org.funql.ri.data.FqlConnectionWithRange
import org.funql.ri.data.FqlDataException
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.util.Named
import org.funql.ri.util.SomethingMissingError
import org.yaml.snakeyaml.Yaml
import org.funql.ri.data.FqlMultiMapContainer


public open class JsonConnection(name: String, propsArg: Map<String?, String?>?): KNamedImpl(name), FqlConnectionWithRange
{
    val props: Map<String?, String?> = propsArg!!

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


    public override fun useIterator(p0: String?): FqlIterator? = useIteratorK(p0!!)
    public fun useIteratorK(streamName: String): FqlIterator? {
        if (streamName != "top")
            throw FqlDataException("Entry point for list must be named top not: " + streamName)
        else if (!(data is ArrayList<*>))
            throw FqlDataException("Entry point for list is not a list: " + streamName)
        return JsonArrayIterator(streamName, data as ArrayList<*>)
    }


    public override fun range(p0: String?, p1: String?, p2: String?, p3: Boolean): FqlIterator? = range1(name = p0!!, startKey= p1!!, endKey = p2!!, includeEnd = p3)
    fun range1(name: String, startKey: String, endKey: String, includeEnd: Boolean): FqlIterator
    {
        if (data is List<*>)
        {
            val start = startKey.toInt()
            val end = endKey.toInt()
            val subList = (data as List<*>).subList(start, end)
            return ListFqlIterator(subList)
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
            return ListFqlIterator(ret)
        }
        else
            throw FqlDataException("Connection '" + getName() + "' is not a range.")
    }

    public override fun useMap(p0: List<String?>?): FqlMapContainer? = useMapK(path = p0!!)
    public fun useMapK(path: List<String?>): FqlMapContainer =
            if (data is Map<*, *>) JsonMapAccess(path[0]!!, data)
            else throw FqlDataException("Connection '" + getName() + "' is not a map.")


    public override fun useMultiMap(p0: List<String?>?): FqlMultiMapContainer? = useMultiMapK(fieldpath = p0!!)
    public fun useMultiMapK(fieldpath: List<String?>): FqlMultiMapContainer =
            if (data is Map<*, *>) JsonMultiMapAccess(fieldpath.get(0)!!, data)
            else throw FqlDataException("Connection '" + getName() + "' is not a map.")

    public override fun close()
    {
        input.close()
    }

    public override fun getMember(p0: Any?, p1: String?): Any? = getMemberK(from = p0!!, member = p1!!)
    public fun getMemberK(from: Any, member: String): Any? =
            if (from is Map<*, *>) from.get(member)
            else null
}


class JsonMapAccess(name: String, val data: Map<*, *>): KNamedImpl(name), FqlMapContainer
{
    public override fun lookup(p0: Any?): Any? = data.get(p0)
}

class JsonMultiMapAccess(name: String, val data: Map<*, *>): KNamedImpl(name), FqlMultiMapContainer
{
    public override fun lookup(p0: Any?): JsonArrayIterator? {
        val ret = data.get(p0)
        if (ret is List<*>)
            return JsonArrayIterator(getName(), ret)
        throw FqlDataException("Entry of multi-map " + getName() + " is not a list. but a " + ret.javaClass)
    }
}

