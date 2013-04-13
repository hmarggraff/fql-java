package org.funql.ri.mongodriver

import org.funql.ri.data.FunqlDriver
import org.funql.ri.data.FunqlConnection
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


    //public override fun openConnection(name: String?, props: Map<String, String>?): FunqlConnection?  = FqlMongoConnectionKt(name!!, props!!)
    public override fun openConnection(p0: String?, p1: Map<String, String>?): FunqlConnection?  = FqlMongoConnectionKt(p0!!, p1!!)

    public override fun supportsRanges() = true

    public override fun isAdvancedDriver() = false
}

class FqlMongoConnectionKt(name: String, val props: Map<String, String?>?): NamedImpl(name), FunqlConnection
{
    public val dbname: String = props!!.get("db")?: throw SomethingMissingError("missing property db for mongo database.")
    public val mongoConn: Mongo = Mongo()
    public val mongoDB: DB = mongoConn.getDB(dbname)?: throw SomethingMissingError("Mongo Database named ${dbname} not found.");
    {
        println(mongoDB)
    }


    //public override fun useIterator(streamName: String?): FqlIterator {
    public override fun useIterator(p0: String?): FqlIterator {
        if (mongoDB.collectionExists(p0!!))
            return FqlMongoIteratorKt(mongoDB.getCollection(p0)?.find()?:throw SomethingMissingError("Collection with name " + p0 + " not Found"))
        else
            throw SomethingMissingError("Collection with name " + p0 + " not Found")
    }
    public override fun close() = mongoConn.close()


    public override fun useMap(p0: List<String>?): FqlMapContainer? = useMapK(fieldpath = p0.check())
    public fun useMapK(fieldpath: List<String>): FqlMapContainer?  {

        if (fieldpath.size() > 1) throw ImplementationLimitation("MongoDB only supports top level Collections");
        if (fieldpath.size() == 0) throw java.lang.AssertionError("The path to open a map in $dbname is empty");
        val streamName = fieldpath[0]
        val coll: DBCollection = mongoDB.getCollection(streamName)?:throw SomethingMissingError("Collection with name " + streamName + " not Found")
        return FqlMongoLookupSingleKt(streamName, coll)
    }


    //public override fun useMultiMap(fieldpath: List<String>?): FqlMultiMapContainer? = useMultiMapK(fieldpath = fieldpath.check())
    public override fun useMultiMap(p0: List<String>?): FqlMultiMapContainer? {
        val fpath = p0.check("fieldpath")
        if (fpath.size() > 1) throw ImplementationLimitation("MongoDB only supports top level Collections");
        if (fpath.size() == 0) throw java.lang.AssertionError("The path to open a map in $dbname is empty");
        val streamName = fpath[0]
        val coll: DBCollection = mongoDB.getCollection(streamName)?:throw SomethingMissingError("Collection with name " + streamName + " not Found")
        return  FqlMongoLookupSomeKt(fpath[0], coll)
    }


    public override fun getMember(p0: Any?, p1: String?): Any? {
        if (p0 == null) return null;
        else if (p0 is Map<*,*>) return p0[p1!!]
        else if (p0 is DBRef){
            val fetch = p0.fetch()
            val value = fetch?.get(p1!!)
            return value;
        }
        throw AssertionError("Mongo member access needs a map, but found a " + p0.javaClass)
    }



    public override fun compareTo(s: Named): Int = name?.compareTo(s.getName()!!)!!
}

class FqlMongoIteratorKt(val data: DBCursor): FqlIterator
{
    var currentVal: DBObject? = null
    public override fun hasNext(): Boolean = data.hasNext()
    public override fun next(): Any? {
        currentVal = data.next();
        return currentVal
    }
    public override fun current() = currentVal
}

class FqlMongoLookupSingleKt(val fieldPath: String, val data: DBCollection): FqlMapContainer
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

        val ret = data.find(query.getTarget())!!
        if (ret.hasNext())
            return ret.next()
        else
            return null
    }
}
class FqlMongoLookupSomeKt(val fieldPath: String, val data: DBCollection): FqlMultiMapContainer
{
    public override fun lookup(p0: Any?): FqlIterator?
    {
        val query = BasicDBObjectWrapper()
        query.put(fieldPath, p0)
        val ret = data.find(query.getTarget())!!
        return FqlMongoIteratorKt(ret)
    }
}





