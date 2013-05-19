package org.funql.ri.jsondriver

import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.ArrayList
import java.util.LinkedHashMap
import org.funql.ri.data.FunqlConnectionWithRange
import org.funql.ri.data.FqlDataException
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.util.Named
import org.funql.ri.util.ConfigurationError
import org.yaml.snakeyaml.Yaml
import org.funql.ri.data.FqlMultiMapContainer
import org.funql.ri.util.ListFqlIterator
import java.io.FileReader


public open class JsonConnection(name: String, propsArg: Map<String, String>?): KNamedImpl(name), FunqlConnectionWithRange
{
    val props: Map<String, String> = propsArg!!

    fun open(): String {
        if (props containsKey "file")
        {
            val file: String = props.get("file")!!
            var instream: FileReader? = null
            try
            {
                instream = FileReader(file)
                val readText = instream?.readText()!!
                return readText
            }
            catch (ex: FileNotFoundException) {
                throw ConfigurationError("Yaml driver did not find the input file specified in the connection properties: " + file)
            }
            finally{
                instream?.close()
            }
        }
        else if (props containsKey "text")
        {
            return props.get("text")!!
        }
        else
            throw ConfigurationError("Yaml driver needs a file (name) to read from.")
    }


    public val input: String = open()
        get() = $input

    val data: Any? = if (props.get("allYamlParts") == "true") Yaml().loadAll(input) else Yaml().load(input)



    //public override fun useIterator(streamName: String?): FqlIterator? {
    public override fun getIterator(p0: String?): FqlIterator? {
        if (p0 != "top") throw FqlDataException("Entry point for list must be named top not: " + p0)
        else if (!(data is ArrayList<*>)) throw FqlDataException("Entry point for list is not a list: " + p0)
        return JsonArrayIterator(p0, data as ArrayList<*>)
    }

    //public override fun range(name: String?, startKey: String?, endKey: String?, includeEnd: Boolean): FqlIterator?  = range1(name!!, startKey!!, endKey!!, includeEnd)
    public override fun range(p0: String?, p1: String?, p2: String?, p3: Boolean): FqlIterator?  = range1(p0!!, p1!!, p2!!, p3)
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

    //public override fun useMap(fieldpath: List<String>?): FqlMapContainer? =
    public override fun useMap(p0: List<String>?): FqlMapContainer? =
            if (p0 == null) throw NullPointerException("field path $p0 may not be null.")
            else if (data is Map<*, *>) JsonMapAccess(p0[0], data)
            else throw FqlDataException("Connection '" + getName() + "' is not a map.")


    //public override fun useMultiMap(fieldpath: List<String>?): FqlMultiMapContainer? =
    public override fun useMultiMap(p0: List<String>?): FqlMultiMapContainer? =
            if (p0 == null) throw NullPointerException("field path $p0 may not be null.")
            else if (data is Map<*, *>) JsonMultiMapAccess(p0.get(0), data)
            else throw FqlDataException("Connection '" + getName() + "' is not a map.")

    public override fun close()
    {
        //
    }
    //public override fun getMember(from: Any?, member: String?): Any?  = if (from is Map<*, *>) from.get(member) else null
    public override fun getMember(p0: Any?, p1: String?): Any?  = if (p0 is Map<*, *>) p0.get(p1) else null
}

