/**
 * Created by hmf on 16.11.13.
 */
/**
 * an updater for MongoDB
 */
import org.funql.ri.mongodriver.FunqlMongoConnection
import com.mongodb.DBCollection
import javax.management.MBeanServerDelegateMBean
import com.mongodb.DBObject
import com.mongodb.BasicDBObject
import com.mongodb.WriteConcern

class MongoUpdater(val coll: DBCollection,
                   val writeConcern: WriteConcern): KUpdater(){

    override fun kput(fieldNames: Array<out String>, value: Array<out Any?>): Any {
        val obj = BasicDBObject(buildMap(fieldNames,value))

        coll.save(obj, writeConcern)
        val commandResult = coll.getDB()!!.getLastError()!!
        commandResult.throwOnError()
        val objectId = obj.getObjectId("_id")
        return objectId!!
    }
    override fun kput(fieldNames: Array<out String>, value: Array<out Any?>, key: Any) {
        val obj = BasicDBObject(buildMap(fieldNames,value))
        obj.set("_id", key)
        coll.save(obj, writeConcern)
        val commandResult = coll.getDB()!!.getLastError()!!
        commandResult.throwOnError()
        val objectId = obj.getObjectId("_id")
    }
    override fun commit() {

    }
}
