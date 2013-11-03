package org.funql.ri.mongodriver

import org.funql.ri.data.FunqlDriver
import org.funql.ri.data.FunqlConnection
import org.funql.ri.util.NamedImpl
import org.funql.ri.util.ConfigurationError
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import org.bson.types.ObjectId
import org.funql.ri.util.Named
import org.funql.ri.mongodriver.workaround.BasicDBObjectWrapper
import com.mongodb.DBRef
import com.mongodb.MongoClient
import org.funql.ri.util.FqlIterator4Iterable
import org.funql.ri.exec.Updater

public class MongoDriver : FunqlDriver {


    //public override fun openConnection(name: String?, props: Map<String, String>?): FunqlConnection?  = FqlMongoConnectionKt(name!!, props!!)
    public override fun openConnection(p0: String?, p1: Map<String, String>?): FunqlConnection? = FunqlMongoConnection(p0!!, p1!!)

    public override fun supportsRanges() = true

    public override fun isAdvancedDriver() = false
}

public class FunqlMongoConnection(name: String, val props: Map<String, String?>) : NamedImpl(name), FunqlConnection
{
    override fun getUpdater(targetName: String?): Updater? {
        return null
    }
    public val dbname: String = props.get("db")?: throw ConfigurationError("missing property db for mongo database.")
    public val mongoConn: MongoClient = createClient(props)
    public val mongoDB: DB = mongoConn.getDB(dbname)?: throw ConfigurationError("Mongo Database named ${dbname} not found.");
    private fun createClient(props: Map<String, String?>): MongoClient {
        val host: String = props["host"]?:"localhost"
        val portString: String? = props["port"]

        if (portString != null)
        {
            try
            {
                val port = Integer.parseInt(portString)
                return MongoClient(host, port)
            }
            catch (x: NumberFormatException)
            {
                throw  ConfigurationError("Port property must be a number, not the text '$portString'")
            }
        }
        return MongoClient()
    }

    //public override fun useIterator(streamName: String?): FqlIterator {
    public override fun getIterator(p0: String?): FqlIterator {
        if (mongoDB.collectionExists(p0!!))
            return FunqlMongoIterator(mongoDB.getCollection(p0)?.find()?:throw ConfigurationError("Collection with name " + p0 + " not Found"))
        else
            throw ConfigurationError("Collection with name " + p0 + " not Found")
    }
    public override fun close() = mongoConn.close()


    public override fun useMap(p0: String?, p1: List<String>?, p2: Boolean): FqlMapContainer? = useMapK(streamName = p0!!, fieldpath = p1, single = p2)
    public fun useMapK(streamName: String, fieldpath: List<String>?, single: Boolean): FqlMapContainer? {

        val coll: DBCollection = mongoDB.getCollection(streamName)?:throw ConfigurationError("Collection with name " + streamName + " not Found")
        val field: String = fieldpath?.makeString(".")?:"_id"

        return FunqlMongoLookup(field, coll, single)
    }

    public override fun getMember(p0: Any?, p1: String?): Any? {
        if (p0 == null) return null;
        else if (p0 is Map<*, *>) {
            return wrapIfIterable(p0[p1!!])
        }
        else if (p0 is DBRef){
            return wrapIfIterable(p0.fetch()?.get(p1!!))
        }
        throw AssertionError("Mongo member access needs a map, but found a " + p0.javaClass)
    }

    private fun wrapIfIterable(value: Any?): Any? = if (value is Iterable<*>) FqlIterator4Iterable(value) else value


    public override fun compareTo(s: Named): Int = name?.compareTo(s.getName()!!)!!
}

public class FunqlMongoIterator(val data: DBCursor) : FqlIterator
{
    public override fun next(): Any? = if (data.hasNext()) data.next() else FqlIterator.sentinel
}

class FunqlMongoLookup(val fieldPath: String,
                       val data: DBCollection,
                       val single: Boolean) : FqlMapContainer
{
    val isId = fieldPath == "_id"
    public override fun lookup(p0: Any?): Any?
    {
        val query = BasicDBObjectWrapper()

        if (isId && p0 is ByteArray && p0.size == 12)
            query.put(fieldPath, ObjectId(p0 as ByteArray))
        else if (p0 is DBRef)
            return p0.fetch()
        else
            query.put(fieldPath, p0)

        val ret: DBCursor = data.find(query.getTarget())!!
        if (single)
        {
            if (ret.hasNext())
                return ret.next()
            else
                return null
        }
        else
            return FunqlMongoIterator(ret)
    }
}





