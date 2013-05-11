package org.fqlsource.fqltest.monkotest1

import java.util.Date
import java.util.HashMap
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.bson.types.ObjectId
import com.mongodb.Mongo
import com.mongodb.DBCollection
import com.mongodb.DBObject
import com.mongodb.BasicDBList
import com.mongodb.DBRef
import com.mongodb.BasicDBObject
import org.funql.ri.test.genericobject.TestObject
import org.funql.ri.test.genericobject.Ref
import org.funql.ri.mongodriver.workaround.BasicDBObjectWrapper
import org.funql.ri.test.cameradata.CameraData
import org.funql.ri.mongokotlinwrapper
import org.bson.BSONObject


fun main(args: Array<String>) {
    try
    {
        val mongoConn: Mongo = Mongo()


        val db = mongoConn.getDB("funql_test")!!

        val homeOrg: DBCollection? = db.getCollection("homeOrg")

        if (homeOrg == null)
            {
                println("Mongo collection homeOrg not found.")
                return
            }

        println("collection homeOrg has ${homeOrg.count()} elements.")
        val obj = homeOrg.findOne()!!

        assertNotNull(obj)
        assert(obj.get("name") == "The Hypothetical Camera Shop")

        val b: List<DBObject> = db.getCollection("orders")!!.find()!!.toArray()!!

        val map = HashMap<Any, Int>()
        b.forEach {
            val cust: Any = it.get("customer")!!
            val cnt: Int? = map.get(cust)
            if (cnt == null) map.put(cust, 1)
            else map.put(cust, cnt+1)
        }
        var minCnt = 10000
        var res: Any? = null;
        map.entrySet().forEach { if (it.getValue() < minCnt) {
            res = it.getKey()
            minCnt = it.getValue()
        } }
        println( "Customer $res with $minCnt orders")
        val custRef: DBRef = res as DBRef
        val customerObj = custRef.fetch()

        println(customerObj)
        mongoConn.close()
    }
    catch(e: Throwable) {
        e.printStackTrace()
    }
}

