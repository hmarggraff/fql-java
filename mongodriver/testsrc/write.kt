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
import org.funql.ri.test.genericobject.Key
import org.funql.ri.test.genericobject.TypeDef
import org.funql.ri.test.genericobject.FieldDef
import org.funql.ri.test.genericobject.Types


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
        val literalArrayType = TypeDef("LiteralArray", FieldDef.str("name"), FieldDef.arr("intarray"), FieldDef.arr("stringarray"), FieldDef.key("local_id"))

        val primeNumbers = intArray(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31)
        val evenNumbers = intArray(2, 4, 6, 8, 10)


        val even: TestObject = TestObject(literalArrayType, "even", evenNumbers, CameraData.orgTypes, CameraData.homeOrg.oid)
        val primes: TestObject = TestObject(literalArrayType, "primes", primeNumbers, CameraData.countryCodes,even.oid)

        println("testStorecameras into funql_test")

        println("adding 1 ${CameraData.homeOrg.typ.name} to collection homeOrg")

        val homeOrg = db.getCollection("homeOrg")!!
        homeOrg.drop()
        homeOrg.insert(toDBObject(CameraData.homeOrg))
        homeOrg.insert(toDBObject(primes))
        homeOrg.insert(toDBObject(even))
        rewriteCollection(productNamespace, org.funql.ri.test.cameradata.CameraData.products)
        rewriteCollection(organisationsNamespace, org.funql.ri.test.cameradata.CameraData.orgs)
        rewriteCollection("orders", org.funql.ri.test.cameradata.CameraData.orders())
        mongoConn.close()
    }
    fun rewriteCollection(name: String, data: Array<TestObject>) {
        println("adding ${data.size} ${data[0].typ.name} to collection $name")
        val coll = db.getCollection(name)!!
        coll.drop()
        data.forEach { coll.insert(toDBObject(it)) }
    }
    fun toDBObject(data: TestObject): DBObject {
        val doc = BasicDBObjectWrapper()
        doc.put("_id", data.oid)
        for (i in 0..data.values.size-1) {
            val v = data.values[i]
            val k = data.typ.fields[i].name
            val pv : Any? = when(v){
                is TestObject -> toDBObject(v)
                is Array<Int> -> literalList(v)
                is Array<String> -> literalList(v)
                is Array<TestObject> -> {
                    val list = BasicDBList()
                    for (to in v) list.add(toDBObject(to))
                    list
                }
                is Ref -> DBRef(db, v.container, (v.target as TestObject).oid)
                else -> v
            }
            doc.put(k,pv);
        }
        return doc.getTarget()!!
    }

    fun literalList(v: Array<out Any>): BasicDBList{
        val list = BasicDBList()
        for (to in v) list.add(to)
        return list
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
