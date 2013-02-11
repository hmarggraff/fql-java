package org.funql.ri.test.kameratest

import java.util.Date
import java.util.HashMap
import com.mongodb.Mongo
import com.mongodb.DBCollection
import com.mongodb.DBObject
import com.mongodb.BasicDBList
import com.mongodb.DBRef
import org.funql.ri.test.genericobject.Ref
import org.funql.ri.mongodriver.workaround.BasicDBObjectWrapper
import org.funql.ri.test.cameradata.CameraData
import org.funql.ri.test.genericobject.TestObject

class TestCameraWrite
{
    val mongoConn = Mongo()

    val db = mongoConn.getDB("funql_test")!!
    val productNamespace = "products"
    val organisationsNamespace = "organisations"

    {
        println("TestCameraWrite init")
    }


    public fun testStoreCameras(): Unit {
        println("testStorecameras")
        fun coll(name: String) = db.getCollection(name)!!

        val homeOrg = coll("homeOrg")
        homeOrg.insert(toDBObject(org.funql.ri.test.cameradata.CameraData.homeOrg))
        addToDB(coll(productNamespace), org.funql.ri.test.cameradata.CameraData.products)
        addToDB(coll(organisationsNamespace), org.funql.ri.test.cameradata.CameraData.orgs)
        addToDB(coll("orders"), org.funql.ri.test.cameradata.CameraData.orders())
        mongoConn.close()
    }
    fun addToDB(coll: DBCollection, data: Array<TestObject>) {
        data.forEach { coll.insert(toDBObject(it)) }
    }
    fun toDBObject(data: TestObject): DBObject {
        val doc = BasicDBObjectWrapper()

        for (i in 0..data.values.size-1) {
            val v = data.values[i]
            val k = data.typ.fields[i].name
            when(v){
                is TestObject -> doc.put(k, toDBObject(v))
                is Array<TestObject> -> {
                    val list = BasicDBList()
                    for (to in v) list.add(toDBObject(to))
                    //list.addAll(v.toCollection())
                    doc.put(k, list)
                }
                is Ref ->
                    doc.put(k, DBRef(db, v.container, (v.target as TestObject).oid))
                else -> doc.put(k, v)
            }
        }
        return doc.getTarget()!!
    }
}

fun main(args: Array<String>) {
    try
    {
    val obj = TestCameraWrite();
    //val oo = obj.productNamespace
    println(obj)
    obj.testStoreCameras()
    }
    catch(e: Throwable) {
        e.printStackTrace()
    }
}
