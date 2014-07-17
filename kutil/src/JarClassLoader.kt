/**
 * Created by hans_m on 26.06.2014.
 */
package org.funql.ri.classloading

import java.net.URLClassLoader
import java.net.URL
import java.util.ArrayList

object JarClassLoader {
    val thisClass = javaClass<JarClassLoader>()
    private var current: ClassLoader = thisClass.getClassLoader()!!
    private var top: ClassLoader = current

    fun loadClassFromJar(classname: String, jar: String): Class<out Any?> {
        try {
            val loadedClass: Class<out Any?>? = current.loadClass(classname)!!
            return loadedClass!!
        } catch(ex: ClassNotFoundException) {
            val newLoader = URLClassLoader(array(URL("file:" + jar)), current)
            val loadedClass: Class<out Any?>? = newLoader.loadClass(classname)!!
            current = newLoader
            return loadedClass!!
        }
    }
    fun loadClass(classname: String): Class<out Any?>? = current.loadClass(classname)

    fun addJars(jars: Array<String>) {
        val urls: Array<URL> = Array<URL>(jars.size) { URL("file:" + jars[it]) }
        current = URLClassLoader(urls, current)
    }

    fun getExtendedClasspath(): Array<String> = buildExtendedClasspath(0, current)

    private fun buildExtendedClasspath(depth: Int, ccl: ClassLoader?): Array<String> {
        if (ccl == top) {
            [suppress("CAST_NEVER_SUCCEEDS")]
            return arrayOfNulls<String>(depth) as Array<String>
        }
        val uccl = ccl as URLClassLoader
        val urls: Array<URL> = uccl.getURLs()
        val ret = buildExtendedClasspath(depth + urls.size, uccl.getParent())
        for (i in 0..urls.size - 1)
            ret[depth + i] = urls[i].getFile()!!
        return ret
    }

    fun forName(name: String):Class<out Any?>  = Class.forName(name, true, current)
}


