package org.funql.ri.test.cameradata

import java.util.Date
import org.funql.ri.test.genericobject.TypeDef
import org.funql.ri.test.genericobject.FieldDef.*
import org.funql.ri.test.RandomGenerator.*
import org.funql.ri.test.genericobject.TestObject
import org.funql.ri.test.genericobject.Ref
import org.funql.ri.test.genericobject.Lid

object CameraData
{
    val employeeType = TypeDef("Employee", str("name"), str("surname"), date("birthDate"), str("job"), str("telno"))
    val dayFields = TypeDef("Day", int("year"), int("month"), int("day"))
    val organisationFields = TypeDef("Organisation", str("name"), str("streetAddress"), str("phoneNumber"), str("city"), str("zipCode"), str("customerId"), str("country"), obj("accountManager"), arr("employees"))
    val cameraFields = TypeDef("Camera", str("name"), float("pixels"), int("wide"), int("tele"), float("price"), int("weight"), str("imagefile"), str("description"))
    val orderItemType = TypeDef("OrderItem", ref("product"), int("units"), float("price"))
    val orgTypes: Array<String> = array("Inc", "SA", "GmbH", "AG", "Ltd", "Oy")
    val countryCodes: Array<String> = array("US", "F", "D", "CH", "GB", "FI")
    val streetTypes: Array<String> = array("Rd", "St", "Ave")
    val countries = array("D", "A", "CH", "F", "B", "NL", "GB", "USA", "CAN", "PL")
    val primeNumbers = intArray(2,3,5,7,11,13,17,19,23,29,31)
    val evenNumbers = intArray(2,4,6,8,10)
    val jobNames = array("CEO", "Programmer", "Assistant", "Hausmeister", "VP Sales", "Accountant", "Scrum master", "Product Owner")
    val shippingStates = array("ordered", "incomplete", "shipped", "paid")
    val orderType = TypeDef("Order", str("orderId"), ref("customer"), arr("items"), float("value"), float("cost"), int("shippingState"), date("date"))
    val literalArrayType = TypeDef("LiteralArray", str("name"), arr("intarray"), arr("stringarray"), lid("other"))


    fun telno(): String = (100 + nextInt(900)).toString() + " " + (100 + nextInt(900)).toString() + (1000 + nextInt(9000)).toString()

    fun day(y: Int, m: Int, d: Int): Int = y * 10000 + m * 100 + d

    public val employees: Array<TestObject> = array(
            TestObject(employeeType, "Newton", "Helmut", day(1952, 8, 24), "Chief executive officer", telno()),
            TestObject(employeeType, "Adams", "Ansel", day(1915, 12, 19), "Sales USA West", telno()),
            TestObject(employeeType, "Ray", "Man", day(1961, 7, 20), "Sales EMEA", telno()),
            TestObject(employeeType, "Capa", "Robert", day(906, 12, 27), "Vertrieb USA Mitte", telno()),
            TestObject(employeeType, "Eisenstaedt", "Alfred", day(1898, 6, 12), "Vertrieb USA Ost", telno()),
            TestObject(employeeType, "Cartier-Bresson", "Henri", day(1940, 10, 9), "Vertrieb Frankreich", telno()),
            TestObject(employeeType, "Hill", "David Octavius", day(1802, 12, 2), "Founder", telno()),
            TestObject(employeeType, "Heidersberger", "Heinrich", day(1923, 12, 2), "Programmer", telno())
    )
    public val homeOrg: TestObject = TestObject(organisationFields, "The Hypothetical Camera Shop", "Fichtenstr. 19", "+49 89 89026748", "Germering", "82110", "ThatsUs", "DE", employees[0], employees, orgTypes)

    public val products: Array<TestObject> = array(
            TestObject(cameraFields, "Olympus SP-560 UZ", 8.0, 27, 486, 550.00, 445, "pix/cam_002.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 640 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "General Imaging GE E850", 8.0, 28, 140, 99.0, 155, "pix/c2_002.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 640 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Nikon Coolpix P50", 8.1, 28, 102, 250.00, 200, "pix/cam_000.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 640 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Sigma DP1", 14.0, 28, 28, 699.0, 240, "pix/c2_003.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Canon Digital Ixus 860 IS", 8.0, 28, 105, 327.00, 210, "pix/cam_003.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 640 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Sony DSC-R1", 10.2, 24, 120, 792.00, 1000, "pix/c2_009.jpg", "Objektiv/Monitor schwenkbar, Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh und Anschlussbuchse), Kabelfernbedienungoptional, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Samsung Digimax Pro815", 8.0, 28, 420, 530.00, 1009, "pix/c2_010.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 640 x 480 Pixel, Infrarotfernbedienungoptional, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Ricoh Caplio GX100", 10.1, 24, 72, 504.00, 250, "pix/c2_001.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 640 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Ricoh Caplio 500G Wide", 8.1, 28, 85, 485.00, 450, "pix/c2_007.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 320 x 240 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Ricoh Caplio R7", 8.1, 28, 200, 350.00, 161, "pix/cam_001.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 640 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Fujifilm FinePix S9600", 9.0, 28, 300, 401.00, 645, "pix/c2_005.jpg", "Objektiv/Monitor schwenkbar, Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh und Anschlussbuchse), Videoaufzeichnung 640 x 480 Pixel, Kabelfernbedienungoptional, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Fujifilm FinePix F480", 8.2, 28, 112, 162.00, 160, "pix/cam_004.jpg", "Blitz-Langzeitsynchronisation, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Fujifilm FinePix S8000fd", 8.0, 27, 486, 400.00, 510, "pix/cam_005.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Casio Exilim EX-Z8", 8.1, 36, 108, 250.0, 178, "pix/c3_000.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 848 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Casio Exilim Hi-Zoom EX-V8", 8.2, 38, 266, 284.00, 200, "pix/c3_001.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 848 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Casio Exilim EX-Z1080", 10.1, 38, 114, 227.00, 190, "pix/c3_002.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 640 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Casio Exilim EX-S880", 8.2, 38, 114, 231.00, 138, "pix/c3_003.jpg", "Blitz-Langzeitsynchronisation, Videoaufzeichnung 848 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Casio Exilim EX-Z77", 7.3, 38, 114, 180.00, 128, "pix/c3_004.jpg", "Blitz-Langzeitsynchronisation, Videoaufzeichnung 848 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Casio Exilim EX-Z1200", 12.1, 37, 111, 308.00, 152, "pix/c3_005.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 848 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Casio Exilim EX-Z11", 7.2, 38, 114, 200.0, 122, "pix/c3_006.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 640 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Casio Exilim EX-Z65", 6.0, 38, 114, 161.00, 122, "pix/c3_007.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 640 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Casio Exilim EX-Z75", 7.2, 38, 114, 175.00, 122, "pix/c3_008.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 640 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Casio Exilim EX-Z1050", 10.1, 38, 114, 217.00, 125, "pix/c3_009.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 640 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Leica C-LUX 1", 6.0, 28, 102, 433.00, 160, "pix/l_005.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 848 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Leica C-Lux 2", 7.3, 28, 100, 500.00, 154, "pix/l_000.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 848 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Leica V-LUX 1", 10.1, 35, 420, 799.00, 734, "pix/l_002.jpg", "Objektiv/Monitor schwenkbar, Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 848 x 480 Pixel, Kabelfernbedienungoptional, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Leica D-LUX 3", 10.2, 28, 112, 599.00, 220, "pix/c2_004.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 1280 x 720 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Panasonic Lumix DMC-LX2", 10.2, 28, 112, 420.00, 217, "pix/c2_006.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 1280 x 720 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Panasonic Lumix DMC-FX33", 8.3, 28, 100, 380.00, 154, "pix/cam_006.jpg", "Blitz-Langzeitsynchronisation, Videoaufzeichnung 848 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Panasonic Lumix DMC-FX55", 8.3, 28, 100, 400.00, 165, "pix/cam_007.jpg", "Blitz-Langzeitsynchronisation, Videoaufzeichnung 848 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Panasonic Lumix DMC-FZ18", 8.3, 28, 504, 480.00, 407, "pix/cam_008.jpg", "Blitz-Langzeitsynchronisation, Videoaufzeichnung 848 x 480 Pixel, Direkt-Druck-Funktion"),
            TestObject(cameraFields, "Panasonic Lumix DMC-FX100", 12.2, 28, 100, 362.00, 176, "pix/cam_009.jpg", "Blitz-Langzeitsynchronisation, Anschluss fuer externen Blitz (Blitzschuh), Videoaufzeichnung 1280 x 720 Pixel ), Direkt-Druck-Funktion")
    )

    public val even: TestObject = TestObject(literalArrayType, "even", evenNumbers, orgTypes, Lid(homeOrg))
    public val primes: TestObject = TestObject(literalArrayType, "primes", primeNumbers, countryCodes, Lid(even))



    public val orgs: Array<TestObject> = array(org(), org(), org(), org(), org(), org(), org(), org(), org(), org()) // init with closure fails due to a bug in kotlin M4

    fun org(): TestObject {
        return TestObject(organisationFields, //
                string8() + " " + pick(orgTypes), //
                (nextInt(100) + 1).toString() + " " + string8() + " " + pick(streetTypes), //
                "" + (nextInt(900) + 100) + " " + (nextInt(900) + 100) + " " + (nextInt(900) + 100), //
                string8(), //
                pick(countryCodes) + " " + nextInt(10000), "C" + nextInt(10000) + "/" + nextInt(64), //
                pick(countries), //
                pick(employees), //
                array(TestObject(employeeType, array(string8(), makeVorname, day(nextInt(70) + 1920, nextInt(12), nextInt(30)), pick(jobNames), telno())),
                        TestObject(employeeType, array(string8(), makeVorname, day(nextInt(70) + 1920, nextInt(12), nextInt(30)), pick(jobNames), telno())))
        )

    }


    public fun orders(): Array<TestObject> = Array<TestObject>(100, { oCnt ->
        var cost: Double = 0.0
        val now = Date(System.currentTimeMillis())
        val newOrder = TestObject(orderType, "QS." + oCnt, Ref(pick(orgs), "organisations"),
                null, 0.0, 0.0, nextInt(3),
                (now.getYear() - 1 - nextInt(5)) * 10000 + (nextInt(12) + 1) * 100 + nextInt(28))
        val items = Array<TestObject>(nextInt(products.size - 1) + 1, {
            val got = Array<Boolean>(products.size, { false })
            val p0 = nextInt(products.size)
            var pp = (p0 + 1) % products.size
            while (got[pp] && pp != p0)
            {
                pp = (pp + 1) % products.size
            }
            got[pp] = true
            val p = products[pp]
            cost = cost + products[pp].values[3] as Int
            TestObject(orderItemType, Ref(p, "products"), nextInt(10) + 1, products[pp].values[4])
        })
        newOrder.values[2] = items
        val margin: Double = nextFloat()
        val factor: Double = margin + 1
        newOrder.values[3] = cost * factor
        newOrder.values[4] = cost
        newOrder
    })

    fun Array<Any>.sum<T: Any>(a: Array<T>, f: (it: T) -> Double): Double {
        var agg: Double = 0.0
        for (el in a) agg += f(el)
        return agg
    }
}

fun main(args: Array<String>) {
    try
    {
        println(CameraData.orgs[0])
    }
    catch(e: Throwable) {
        //e.printStackTrace()
        e.getCause()?.printStackTrace()
    }
}




