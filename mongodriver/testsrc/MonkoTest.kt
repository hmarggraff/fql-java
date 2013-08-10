package org.fqlsource.fqltest.monkotest

import org.testng.annotations.Test
import kotlin.test.assertNotNull
import org.testng.annotations.DataProvider
import org.funql.ri.data.FqlIterator
import org.funql.ri.parser.FqlParser
import org.funql.ri.kotlinutil.javaHashMap
import org.funql.ri.data.FunqlConnection
import org.testng.annotations.BeforeClass
import org.testng.annotations.AfterClass
import com.mongodb.DBObject
import org.funql.ri.mongodriver.MongoDriver
import org.funql.ri.test.util.dump
import java.net.ConnectException

class
MonkoTest
{
    val dbName = "funql_test"
    var conn: FunqlConnection? = null
    var productsIterator: FqlIterator? = null
    val products = "products"

    BeforeClass public fun mongoconnection() {
        val driver = MongoDriver()
        val kmap: Map<String, String> = javaHashMap("db" to dbName)
        println("got driver ${driver}")
        conn = driver.openConnection(dbName, kmap) //as FqlMongoConnectionKt
        assertNotNull(conn, "Connection $dbName could not be created.")
        try {

            productsIterator = conn?.getIterator(products)
        }       catch (x: ConnectException) {
            println(" Is the database running?")
            throw x
        }
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
             /*
            array("from homeOrg", "[{_id:'9',name:'The Hypothetical Camera Shop',streetAddress:'Fichtenstr. 19',phoneNumber:'+49 89 89026748',city:'Germering',zipCode:'82110',customerId:'ThatsUs',country:'DE',accountManager:{_id:'1',name:'Newton',surname:'Helmut',birthDate:'19520824',job:'Chief executive officer',telno:'162 1527303'},employees:[{_id:'1',name:'Newton',surname:'Helmut',birthDate:'19520824',job:'Chief executive officer',telno:'162 1527303'},{_id:'2',name:'Adams',surname:'Ansel',birthDate:'19151219',job:'Sales USA West',telno:'758 9677105'},{_id:'3',name:'Ray',surname:'Man',birthDate:'19610720',job:'Sales EMEA',telno:'611 1461462'},{_id:'4',name:'Capa',surname:'Robert',birthDate:'9061227',job:'Vertrieb USA Mitte',telno:'527 4922508'},{_id:'5',name:'Eisenstaedt',surname:'Alfred',birthDate:'18980612',job:'Vertrieb USA Ost',telno:'552 3604962'},{_id:'6',name:'Cartier-Bresson',surname:'Henri',birthDate:'19401009',job:'Vertrieb Frankreich',telno:'952 8695476'},{_id:'7',name:'Hill',surname:'David Octavius',birthDate:'18021202',job:'Founder',telno:'362 1483075'},{_id:'8',name:'Heidersberger',surname:'Heinrich',birthDate:'19231202',job:'Programmer',telno:'669 7226203'}],orgTypes:['Inc','SA','GmbH','AG','Ltd','Oy']},{_id:'43',name:'primes',intarray:['2','3','5','7','11','13','17','19','23','29','31'],stringarray:['US','F','D','CH','GB','FI'],local_id:'42'},{_id:'42',name:'even',intarray:['2','4','6','8','10'],stringarray:['Inc','SA','GmbH','AG','Ltd','Oy'],local_id:'9'}]"),
            array("from homeOrg select name", "['The Hypothetical Camera Shop','primes','even']"),
            array("from homeOrg where name = \"even\"", "{_id:'42',name:'even',intarray:['2','4','6','8','10'],stringarray:['Inc','SA','GmbH','AG','Ltd','Oy'],local_id:'9'}"),
            array("from homeOrg where name = \"even\" select intarray", "['2','4','6','8','10']"),
            array("from homeOrg where name = \"even\" select from it.intarray end", "['2','4','6','8','10']"),*/
            array("from homeOrg where name = \"even\" select from it.intarray where it > 9 select it +1 end", "[{f:11}]") /*,
            array("from products where name like \"Olympus*\" select name", "'Olympus SP-560 UZ'"),
            array("from products where name like \"Ricoh*\" select name", "['Ricoh Caplio GX100','Ricoh Caplio 500G Wide','Ricoh Caplio R7']"),
            array("from orders where orderId = \"QS.2\" select orderId, customer.name", "{orderId:'QS.2',customer_name:'Atufotra Ltd'}"),
            array("from orders where customer.name = \"Friulpe Inc\" select orderId, customer.zipCode", "[{orderId:'QS.17',customer_zipCode:'D 9359'},{orderId:'QS.99',customer_zipCode:'D 9359'}]"),

            array("from orders where customer.name = \"Friulpe Inc\" select customer", "['{ \"\$ref\" : \"organisations\", \"\$id\" : \"70\" }','{ \"\$ref\" : \"organisations\", \"\$id\" : \"70\" }']"),
            array("from orders where orderId = \"QS.2\" select orderId, from funql_test.organisations where up(1).customer.customerId = customerId select name end", "{orderId:'QS.2',organisations:[{name:'Atufotra Ltd'}]}"),
            //array("from orders where orderId = \"QS.2\" select orderId, from customers get customer end", "{f1:'QS.2',f2:'Atufotra Ltd'}"),
            array("link organisations by _id from orders where orderId = \"QS.2\" select orderId, organisations[customer].name", "{orderId:'QS.2',_name:'Atufotra Ltd'}"),
            //array("from orders where orderId = \"QS.2\" select orderId, customer -> organisations.id", "{orderId:'QS.13',_name:'Inaplefi SA'}"),
            array("link organisations by _id from orders where orderId = \"QS.13\" select orderId, organisations[customer].name", "{orderId:'QS.13',_name:'Inaplefi SA'}"),
            array("from products select name limit 2", "['Olympus SP-560 UZ','General Imaging GE E850']"),
            array("from homeOrg select name", "['The Hypothetical Camera Shop','primes','even']") */
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


    AfterClass fun close() {
        conn?.close()
    }

}

