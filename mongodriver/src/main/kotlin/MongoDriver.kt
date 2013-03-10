package org.funql.ri.mongodriver

import org.funql.ri.data.FunqlDriver
import org.funql.ri.data.FqlConnection
import org.funql.ri.util.NamedImpl
import org.funql.ri.util.SomethingMissingError
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.util.ImplementationLimitation
import org.funql.ri.kotlinutil.dotJoin
import com.mongodb.Mongo
import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.DBObject
import org.bson.types.ObjectId
import org.funql.ri.util.Named
import org.funql.ri.data.FqlMultiMapContainer
import org.funql.ri.kotlinutil.check
import org.funql.ri.mongodriver.workaround.BasicDBObjectWrapper
import com.mongodb.DBRef


class MongoDriverKt: FunqlDriver {


    public override fun openConnection(name: String?, props: Map<String, String>?): FqlConnection?  = FqlMongoConnectionKt(name!!, props!!)

    public override fun supportsRanges() = true

    public override fun isAdvancedDriver() = false
}

class FqlMongoConnectionKt(name: String, val props: Map<String, String?>?): NamedImpl(name), FqlConnection
{
    public val dbname: String = props!!.get("db")?: throw SomethingMissingError("missing property db for mongo database.")
    public val mongoConn: Mongo = Mongo()
    public val mongoDB: DB = mongoConn.getDB(dbname)?: throw SomethingMissingError("Mongo Database named ${dbname} not found.");
    {
        println(mongoDB)
    }


    public override fun useIterator(streamName: String?): FqlIterator {
        if (mongoDB.collectionExists(streamName!!))
            return FqlMongoIteratorKt(mongoDB.getCollection(streamName)?.find()?:throw SomethingMissingError("Collection with name " + streamName + " not Found"))
        else
            throw SomethingMissingError("Collection with name " + streamName + " not Found")
    }
    public override fun close() = mongoConn.close()


    public override fun useMap(fieldpath: List<String>?): FqlMapContainer? = useMapK(fieldpath = fieldpath.check())
    public fun useMapK(fieldpath: List<String>): FqlMapContainer?  {

        if (fieldpath.size() > 1) throw ImplementationLimitation("MongoDB only supports top level Collections");
        if (fieldpath.size() == 0) throw java.lang.AssertionError("The path to open a map in $dbname is empty");
        val streamName = fieldpath[0]
        val coll: DBCollection = mongoDB.getCollection(streamName)?:throw SomethingMissingError("Collection with name " + streamName + " not Found")
        return FqlMongoLookupSingleKt(streamName, coll)
    }


    //public override fun useMultiMap(fieldpath: List<String>?): FqlMultiMapContainer? = useMultiMapK(fieldpath = fieldpath.check())
    public override fun useMultiMap(fieldpath: List<String>?): FqlMultiMapContainer? {
        val fpath = fieldpath.check("fieldpath")
        if (fpath.size() > 1) throw ImplementationLimitation("MongoDB only supports top level Collections");
        if (fpath.size() == 0) throw java.lang.AssertionError("The path to open a map in $dbname is empty");
        val streamName = fpath[0]
        val coll: DBCollection = mongoDB.getCollection(streamName)?:throw SomethingMissingError("Collection with name " + streamName + " not Found")
        return  FqlMongoLookupSomeKt(fpath[0], coll)
    }


    public override fun getMember(from: Any?, member: String?): Any? {
        if (from == null) return null;
        else if (from is Map<*,*>) return from[member!!]
        else if (from is DBRef){
            val fetch = from.fetch()
            val value = fetch?.get(member!!)
            return value;
        }
        throw AssertionError("Mongo member access needs a map, but found a " + from.javaClass)
    }


    public override fun compareTo(other: Named): Int = name?.compareTo(other.getName()!!)!!
}

class FqlMongoIteratorKt(val data: DBCursor): FqlIterator
{
    var currentVal: DBObject? = null
    public override fun hasNext(): Boolean = data.hasNext()
    public override fun next(): Object? {
        currentVal = data.next();
        return currentVal
    }
    public override fun current() = currentVal
}

class FqlMongoLookupSingleKt(val fieldPath: String, val data: DBCollection): FqlMapContainer
{
    val isId = fieldPath == "_id"
    public override fun lookup(key: Any?): Any?
    {
        val query = BasicDBObjectWrapper()

        if (isId && key is ByteArray && key.size == 12)
            query.put(fieldPath, ObjectId(key as ByteArray))
        else if (key is DBRef)
            return key.fetch()
        else
            query.put(fieldPath, key)

        val ret = data.find(query.getTarget())!!
        if (ret.hasNext())
            return ret.next()
        else
            return null
    }
}
class FqlMongoLookupSomeKt(val fieldPath: String, val data: DBCollection): FqlMultiMapContainer
{
    public override fun lookup(key: Any?): FqlIterator?
    {
        val query = BasicDBObjectWrapper()
        query.put(fieldPath, key)
        val ret = data.find(query.getTarget())!!
        return FqlMongoIteratorKt(ret)
    }
}





