package org.fqlsource.fqltest.monkotest

import java.util.HashMap
import org.testng.annotations.Test
import kotlin.test.assertNotNull
import org.testng.annotations.AfterTest
import com.mongodb.DB
import org.funql.ri.mongodriver.FunqlMongoConnection
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
import org.funql.ri.data.FunqlConnection
import org.testng.annotations.BeforeClass
import com.sun.org.glassfish.gmbal.NameValue
import org.funql.ri.exec.NamedValue
import org.funql.ri.exec.NamedBoolean
import org.funql.ri.exec.NamedDouble
import org.funql.ri.exec.NamedLong
import org.testng.annotations.AfterClass
import com.mongodb.DBObject
import org.funql.ri.mongodriver.MongoDriverKt

class MonkoTest
{
    val dbName = "funql_test"
    var conn: FunqlConnection? = null
    var productsIterator: FqlIterator? = null
    val products = "products"

    BeforeClass public fun mongoconnection() {
        val driver = MongoDriverKt()
        val kmap: Map<String, String> = javaHashMap("db" to dbName)
        println("got driver ${driver}")
        conn = driver.openConnection(dbName, kmap) //as FqlMongoConnectionKt
        assertNotNull(conn, "Connection $dbName could not be created.")
        productsIterator = conn?.getIterator(products)
        println("got products iterator $productsIterator")
        assertNotNull(productsIterator, "Iterator $products could not be opened. (Did you start mongod?)")

    }


    Test(dataProvider = "query")  // , dependsOnMethods= array("iterateProducts"))
    fun testQuery(query: String, expected: String) {

        val result = run(query)
        println(result)
        if (expected != result) throw AssertionError(" Expected: $expected found $result.")
    }

    Test
            fun iterateProducts() {
        val pi = productsIterator!!
        val current: Any? = pi.next()
        assertNotNull(current, "Iterator $products did not yield data.")
        assert(current != FqlIterator.sentinel, "Iterator $products did not yield data.")
        println(current)
        var cnt = 1
        while (true)
        {
            val item = pi.next()
            if (item == FqlIterator.sentinel) break;
            val imagefile = (item as DBObject).get("imagefile") as String
            assert(imagefile.startsWith("pix/"))
            cnt++
        }
        println("cnt = $cnt")
        assert(cnt == 32, "Wrong number of products")
    }

    DataProvider(name = "query") fun createData1() = array(

            array("from homeOrg where name = \"nonexisten\"", ""),
            array("from products where name like \"Olympus*\" select name", "'Olympus SP-560 UZ'"),
            array("from products where name like \"Ricoh*\" select name", "['Ricoh Caplio GX100','Ricoh Caplio 500G Wide','Ricoh Caplio R7']"),
            array("from orders where orderId = \"QS.2\" select orderId, customer.name", "{f1:'QS.2',f2:'Atufotra Ltd'}"),
            array("from orders where customer.name = \"Friulpe Inc\" select orderId, customer.zipCode", "[{f1:'QS.17',f2:'D 9359'},{f1:'QS.99',f2:'D 9359'}]"),
            array("from orders where customer.name = \"Friulpe Inc\" select customer", "['{ \"\$ref\" : \"organisations\", \"\$id\" : \"68\" }','{ \"\$ref\" : \"organisations\", \"\$id\" : \"68\" }']"),
            array("from orders where orderId = \"QS.2\" select orderId, from organisations where it(2).customer.customerId = customerId select name end", "{f1:'QS.2',f2:[{f1:'Atufotra Ltd'}]}"),
            //array("from orders where orderId = \"QS.2\" select orderId, from customers get customer end", "{f1:'QS.2',f2:'Atufotra Ltd'}") //,
            array("link organisations by _id from orders where orderId = \"QS.2\" select orderId, organisations[customer].name", "{f1:'QS.2',f2:'Atufotra Ltd'}"),
            //array("from orders where orderId = \"QS.2\" select orderId, customer -> organisations.id", "The Hypothetical Camera Shop"),
            //array("link organisations by _id from orders where orderId = \"QS.13\" select orderId, organisations[customer].name", "The Hypothetical Camera Shop"),
            //array("from products select name", "[Panasonic Lumix DMC-FX100, Ricoh Caplio GX100]"),
            array("from homeOrg select name", "'The Hypothetical Camera Shop'")
    )

    fun run(query: String): String {
        print("$query --> ")
        val it = FqlParser.runQuery(query, null, conn)!!
        val sb = StringBuffer()
        var cnt = 0
        while (true) {
            val obj = it.next()
            if (obj == FqlIterator.sentinel) break;
            if (cnt == 1) sb.insert(0, '[')
            if (cnt > 0) sb.append(',')
            cnt++

            if (obj is Array<Any?>)
                dump(if (obj.size == 1) obj[0] else obj, sb, 0)
            else
                dump(obj, sb, 0)
        }
        if (cnt > 1) sb.append(']')
        return sb.toString()
    }


    fun dump(s: Any?, sb: StringBuffer, indent: Int) {
        if (s is Map<*,*>) {
            sb.append('{')
            var cnt = 0
            val entrySet: Set<Map.Entry<Any?, Any?>> = s.entrySet()
            for (e in entrySet) {
                if (cnt > 0) sb.append(',')
                cnt++
                sb.append(e.getKey()).append(':')
                dump(e.getValue(), sb, indent + 1)
            }
            sb.append("}")

        }
        else if (s is NamedLong || s is NamedDouble || s is NamedBoolean) {
            val s1 = (s as NamedValue)
            sb.append(s1.getVal())
        }
        else if (s is NamedValue) {
            //sb.append('\'')
            dump(s.getVal(), sb, indent+1)
            //sb.append('\'')
        }
        else if (s is Array<Any?>) {
            if (s is Array<NamedValue?>)
                System.currentTimeMillis()
            sb.append('{');
            var cnt = 0
            s.forEach {
                if (cnt > 0) sb.append(',')
                cnt++
                sb.append((it as NamedValue).getName()).append(':')
                dump(it, sb, indent + 1)
            }
            sb.append("}")
        }
        else if (s is List<Any?>) {
            sb.append('[');
            var cnt = 0
            s.forEach {
                if (cnt > 0) sb.append(',')
                cnt++
                dump(it, sb, indent + 1)
            }
            sb.append("]")
        }
        else {
            sb.append('\'').append(s.toString()).append('\'')
        }
    }

    AfterClass fun close() {
        conn?.close()
    }

}

