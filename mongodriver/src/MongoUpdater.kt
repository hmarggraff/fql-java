package org.funql.ri.mongodriver

/**
 * Created by hmf on 16.11.13.
 */
/**
 * an updater for MongoDB
 */
import com.mongodb.DBCollection
import com.mongodb.BasicDBObject
import com.mongodb.WriteConcern
import org.funql.ri.data.NamedValues
import org.funql.ri.kotlinutil.KUpdater
import org.funql.ri.util.NamedValuesImpl

class MongoUpdater(val coll: DBCollection,
                   fieldNames: Array<out String>,
                   val writeConcern: WriteConcern): KUpdater(fieldNames){

    override fun kput(values: Array<out Any?>): NamedValues {
        val obj = BasicDBObject(buildMap(values))

        coll.save(obj, writeConcern)
        val commandResult = coll.getDB()!!.getLastError()!!
        commandResult.throwOnError()
        val objectId = obj.getObjectId("_id")!!
        return NamedValuesImpl("_id", objectId)
    }
    override fun kput(values: Array<out Any?>, key: Any) {
        val obj = BasicDBObject(buildMap(values))
        obj.set("_id", key)
        coll.save(obj, writeConcern)
        val commandResult = coll.getDB()!!.getLastError()!!
        commandResult.throwOnError()
        //val objectId = obj.getObjectId("_id")
    }
    override fun commit() {

    }
}
