package org.funql.ri.mongodriver

import org.funql.ri.data.FunqlDriver
import org.funql.ri.kotlinutil.KFunqlConnection
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
import com.mongodb.WriteConcern
import java.util.InvalidPropertiesFormatException
import org.funql.ri.data.FunqlConnection
import java.util.UUID

public class MongoDriver : FunqlDriver {


    public override fun openConnection(name: String?, props: Map<String, String>?): FunqlConnection? = FunqlMongoConnection(name!!, props!!)

    public override fun supportsRanges() = true

    public override fun isAdvancedDriver() = false

}

public class FunqlMongoConnection(name: String, val props: Map<String, String?>) : KFunqlConnection(name)
{
    public val dbname: String = props.get("db")?: throw ConfigurationError("missing property db for mongo database.")
    public val mongoConn: MongoClient = createClient(props)
    public val mongoDB: DB = mongoConn.getDB(dbname)?: throw ConfigurationError("Mongo Database named ${dbname} not found.");
    public val writeConcern: WriteConcern = { val wc = props.get("writeConcern")
        if (wc != null) WriteConcern.valueOf(wc)?:throw InvalidPropertiesFormatException("No MongoDB write concern named: " + wc)
        else WriteConcern.NORMAL
    }()
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
    override fun kgetIterator(streamName: String): FqlIterator {
        if (mongoDB.collectionExists(streamName))
            return FunqlMongoIterator(mongoDB.getCollection(streamName)?.find()?:throw ConfigurationError("Collection with name " + streamName + " not Found"))
        else
            throw ConfigurationError("Collection with name " + streamName + " not Found")
    }

    override fun kgetUpdater(targetName: String): Updater{
        if (mongoDB.collectionExists(targetName))
            return MongoUpdater(mongoDB.getCollection(getName())!!, writeConcern)
        else
            throw ConfigurationError("Collection with name " + targetName + " not Found")
    }

    public override fun close() = mongoConn.close()


    override fun kuseMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer {
        val coll: DBCollection = mongoDB.getCollection(name)?:throw ConfigurationError("Collection with name " + name + " not Found")
        val field: String = fieldpath.makeString(".")

        return FunqlMongoLookup(field, coll, single)
    }


    override fun krange(name: String, startKey: String, endKey: String, includeEnd: Boolean): FqlIterator {
        throw UnsupportedOperationException()
    }
    override fun kgetMember(from: Any?, member: String): Any? {
        if (from == null) return null;
        else if (from is Map<*, *>) {
            return wrapIfIterable(from[member])
        }
        else if (from is DBRef){
            return wrapIfIterable(from.fetch()?.get(member))
        }
        throw AssertionError("Mongo member access needs a map, but found a " + from.javaClass)
    }

    private fun wrapIfIterable(value: Any?): Any? = if (value is Iterable<*>) FqlIterator4Iterable(value) else value


    public override fun compareTo(s: Named): Int = name1.compareTo(s.getName()!!)



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





