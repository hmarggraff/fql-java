package org.fqlsource.fqltest.monkotest

import org.funql.ri.mongodriver.MongoDriverKt
import org.funql.ri.kotlinutil.javaHashMap
import java.util.HashMap
import org.testng.annotations.Test
import kotlin.test.assertNotNull
import org.testng.annotations.AfterTest
import com.mongodb.DB
import org.funql.ri.mongodriver.FqlMongoConnectionKt
import kotlin.test.assertEquals
import org.testng.annotations.DataProvider
import java.util.ArrayList
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.representer.Representer
import org.bson.types.ObjectId
import org.yaml.snakeyaml.nodes.Node
import com.mongodb.BasicDBObject
import org.funql.ri.data.FqlIterator
import org.funql.ri.parser.FqlParser
import org.funql.ri.kotlinutil.javaHashMap
import org.funql.ri.data.FqlConnection


class Tests
{
    val dbName = "funql_test"
    var conn: FqlConnection? = null
    var productsIterator: FqlIterator? = null
    val products = "products"

    Test public fun mongoconnection() {
        val driver = MongoDriverKt()
        val kmap: Map<String?, String?> = javaHashMap("db" to dbName)
        println(driver)
        conn = driver.openConnection(dbName, kmap) //as FqlMongoConnectionKt
        assertNotNull(conn, "Connection $dbName could not be opened.")
    }

    Test(dependsOnMethods = array("mongoconnection")) fun productsIterator() {
        productsIterator = conn?.useIterator(products)
        println(productsIterator)
        assertNotNull(productsIterator, "Iterator $products could not be opened.")
    }

    Test(dataProvider = "query", dependsOnMethods = array("mongoconnection"))
            fun testQuery(query: String, expected: String) {

        val result = run(query)
        assertEquals(expected, result)
    }

    /*
    Test(dependsOnMethods = array("productsIterator"), enabled = false) fun products() {
        val iter = productsIterator!!
        while (iter.hasNext())
        {
            val elem: Any? = iter.next()
            println(elem)
            assertNotNull(elem, "Iterator contained a null")
        }
        assertNotNull(conn, "Iterator $products could not be opened.")
    }
    */

    DataProvider(name = "query")
            fun createData1() = array(
            array("access organisations by _id from orders where orderId = \"QS.2\" select orderId, organisations[customer]", "The Hypothetical Camera Shop") //,
            //array("access organisations by _id from orders where orderId = \"QS.13\" select orderId, organisations[customer].name", "The Hypothetical Camera Shop") //,
            //array("from products select name", "[Panasonic Lumix DMC-FX100, Ricoh Caplio GX100]"),
            //array("from homeOrg select name", "The Hypothetical Camera Shop")
    )



    fun run(query: String): String {
        print("$query --> ")
        val it = FqlParser.runQuery(query, null, conn)!!
        val sb = StringBuffer()
        while (it.hasNext()) {
            val obj = it.next()!! as Array<Any>
            if (obj.size == 1)
            {
                dump(obj[0], sb, 0)
            };
            else
            {
                dump(obj, sb, 0)
            }

        }
        return sb.toString()
    }



    fun dump(s: Any?, sb: StringBuffer, indent : Int) {
        //var neednewline = false;
        when (s) {
            is BasicDBObject -> {
                //neednewline = newline(neednewline, indent, sb);
                sb.append('{')
                val mutableSet: MutableSet<MutableMap.MutableEntry<String, Any>> = s.entrySet()
                for (e in mutableSet) {
                    sb.append(e.getKey()).append(':')
                    dump(e.getValue(), sb, indent +1)
                    //neednewline = true
                }
                sb.append("}")
                //neednewline = true

            }
            is Array<Any?> -> {
                //neednewline = newline(neednewline, indent, sb);
                val arr: Array<Any?> = s
                sb.append('[');
                arr.forEach {
                    dump(it, sb, indent+1)
                    //neednewline = true

                }
                sb.append("]")
                //neednewline = true
            }
             is Int -> {
                //neednewline = newline(neednewline, indent, sb);
                 val s1 = s.toString()
                 sb.append('"').append(s1).append("\" ")


             }
            else -> {
                //neednewline = newline(neednewline, indent, sb);

                sb.append('"').append(s.toString()).append("\" ")
            }
        }
    }

    fun newline(needed: Boolean, indent: Int, sb: StringBuffer): Boolean
    {
        if (!needed) return needed
        sb.append('\n')
        for (i in 1 .. indent) sb.append(' ')
        return false
    }

    public inline fun StringBuffer.plus(s: Any?): StringBuffer {
        this.append(s); return this
    }

    AfterTest fun close() {
        conn?.close()
    }

}

