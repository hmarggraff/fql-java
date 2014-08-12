package org.funql.ri.classloading
/**
 * Created by hans_m on 30.06.2014.
 */
import org.testng.annotations.Test
import java.net.URLClassLoader
import java.net.URL
import kotlin.test.assertNotNull
import java.io.File

class JarClassLoaderTest()
{
    val missing = !(File("jars/ojdbc.jar").exists() && File("jars/hsqldb.jar").exists())

    Test fun testHardcoded() {
        if (missing) return
        val classLoader = URLClassLoader(array(URL("file:jars/ojdbc.jar")))
        val loadedClass: Class<out Any?>? = classLoader.loadClass("oracle.jdbc.OracleDriver")
        assertNotNull(loadedClass)
    }

    Test fun testDynamic1() {
        if (missing) return
        val classFromJar = JarClassLoader.loadClassFromJar("oracle.jdbc.OracleDriver", "jars/ojdbc.jar")

        assertNotNull(classFromJar)
        assert("oracle.jdbc.OracleDriver".equals(classFromJar!!.getName()))
    }

    Test fun testURLList() {
        if (missing) return
        JarClassLoader.addJars(array("jars/ojdbc.jar", "jars/hsqldb.jar"))
        val strings: Array<String> = JarClassLoader.getExtendedClasspath()
        assertNotNull(strings)
        assert(strings.size == 2)
        assert("jars/ojdbc.jar".equals(strings[0]))
        assert("jars/hsqldb.jar".equals(strings[1]))
    }

    Test fun testCascading() {
        if (missing) return
        JarClassLoader.addJars(array("jars/hsqldb.jar"))
        val classFromJar = JarClassLoader.loadClass("org.hsqldb.jdbcDriver")
        assert("org.hsqldb.jdbcDriver".equals(classFromJar!!.getName()))

        val strings: Array<String> = JarClassLoader.getExtendedClasspath()
        assertNotNull(strings)
        assert(strings.size == 2)
        assert("jars/ojdbc.jar".equals(strings[1]))
        assert("jars/hsqldb.jar".equals(strings[0]))
    }

}