package org.funql.ri.gui

import java.io.File
import java.io.FileWriter
import java.io.FileReader
import org.funql.ri.data.FunqlConnection
import org.funql.ri.jsondriver.JsonConnection
import java.util.ArrayList
import org.funql.ri.parser.FqlParser
import org.funql.ri.mongodriver.FunqlMongoConnection
import com.mongodb.DBRef
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.NamedValues
import org.yaml.snakeyaml.Yaml
import org.funql.ri.kotlinutil.NamedString
import org.funql.ri.sisql.SiSqlConnection
import java.io.FileInputStream
import org.funql.ri.kotlinutil.NamedStringPair
import java.util.HashMap
import java.util.SortedMap
import java.util.TreeMap
import com.sun.javafx.collections.transformation.SortedList

class RunnerControl(val view: RunnerView) {

    public final val conNameKey: String = "conName"
    public final val textKey: String = "text"
    public final val fileKey: String = "file"
    public final val dbKey: String = "db"
    public final val hostKey: String = "host"
    public final val portKey: String = "port"
    public final val userKey: String = "user"
    public final val passwdKey: String = "password"
    public final val driverKey: String = "driver_class"
    public final val connectionUrlKey: String = "connection"

    var textChanged = false;
    var funqlFile: File? = null;
    val connections = ArrayList<FunqlConnection>()
    private var _jdbcDrivers: MutableMap<String, Map<String,String>>? = null



    fun writeFile(file: File, text: String) {
        var w: FileWriter? = null
        try {
            w = FileWriter(file)
            w?.write(text)
            textChanged = false
        } finally {
            w?.close()
        }
    }


    fun saveFile(force: Boolean, result: Boolean = false): Unit {
        if (funqlFile == null || force || result) {
            val file = view.showSaveDialog(if (result) "Save Results" else "Save Query")
            if (file != null)
                writeFile(file, if (result) view.getResultText() else view.getQueryText())
        } else
            writeFile(funqlFile!!, if (result) view.getResultText() else view.getQueryText())
    }

    fun changesSavedOrCancelled(): Boolean {
        if (textChanged) {
            val userAnswer = view.askForSave()
            if (userAnswer == UserAnswer.cancel) return true
            if (userAnswer == UserAnswer.yes) saveFile(false)
        }
        return false;
    }

    fun clear(): Unit {
        if (changesSavedOrCancelled()) return
        funqlFile = null
        view.setQueryText("")
    }

    fun openFile(): Unit {
        if (changesSavedOrCancelled()) return
        val file = view.showOpenDialog("Open query file")
        if (file != null && file.canRead()) {
            val r = FileReader(file)
            val s = r.readText()

            view.setQueryText(s)
            r.close()
            funqlFile = file;
            view.setTitle(funqlFile!!.getPath())
        } else
            view.error("File ${file} could not be opened.")
    }
    public fun createJsonConnection(props: MutableMap<String, String>): Unit {
        val ret = JsonConnection(props[conNameKey]!!, props)
        connections.add(ret)
        props.set("driverType", "json")
        org.funql.ri.gui.prefs.saveConnection(props)
    }
    public fun createMongoConnection(props: MutableMap<String, String>): Unit {
        val ret = FunqlMongoConnection(props[conNameKey]!!, props)
        connections.add(ret)
        props.set("driverType", "mongo")

        org.funql.ri.gui.prefs.saveConnection(props)
    }

    public fun createJdbcConnection(props: MutableMap<String, String>): Unit {
        val ret = SiSqlConnection(props[conNameKey]!!, props)
        connections.add(ret)
        props.set("driverType", "jdbc")

        org.funql.ri.gui.prefs.saveConnection(props)
    }

    public fun createConnection(props: MutableMap<String, String>): Unit {
        val drivertype = props["driverType"]
        if ("json".equals(drivertype)) createJsonConnection(props)
        else if ("mongo".equals(drivertype)) createMongoConnection(props)
        else if ("jdbc".equals(drivertype)) createJdbcConnection(props)
    }

    public fun run(q: String) {
        try {

            val it = FqlParser.runQuery(q, null, connections)

            if (it == null) return
            val sb = StringBuffer()
            var cnt = 0
            while (true) {
                if (cnt == 1) sb.insert(0, '[')
                if (cnt > 0) sb.append(",\n")
                cnt++

                val obj = it.next()!!;
                if (obj == FqlIterator.sentinel) break
                else if (obj is Array<Any?>) {
                    val singleton = obj.size == 1
                    dump(if (singleton) obj[0] else obj, sb, 0, singleton)
                } else
                    dump(obj, sb, 0, false)
            }
            if (cnt > 1) sb.append(']')
            view setResultText sb.toString()
        } catch (r: Throwable) {
            r.printStackTrace()
            view.error(r.getMessage()!!)
        }

    }
    public fun close(conn: FunqlConnection) {
        conn.close()
        connections.remove(conn)
    }

    fun dump(s: Any?, sb: StringBuffer, indent: Int, inObject: Boolean) {

        //newline(neednewline, indent, sb)
        if (s is Map<*, *>) {
            newline(s.size > 2, indent, sb)
            sb.append("{")
            var cnt = 0
            val entrySet: Set<Map.Entry<Any?, Any?>> = s.entrySet()
            for (e in entrySet) {
                if (cnt > 0) sb.append(',')
                cnt++
                sb.append(e.getKey()).append(':')
                dump(e.getValue(), sb, indent + 1, true)
            }
            sb.append("}")

        } else if (s is Iterable<Any?>) {
            val arr: Iterable<Any?> = s
            sb.append('[');
            var cnt = 0
            arr.forEach {
                if (cnt > 0) sb.append(',')
                cnt++
                newline(true, indent, sb)
                //sb.append((it as NamedValueImpl).getName()).append(':')
                dump(it, sb, indent + 1, false)
            }
            newline(true, indent, sb)
            sb.append("]")
        } else if (s is DBRef) {
            sb.append(s.toString())
        } else {
            //neednewline = newline(neednewline, indent, sb);
            sb.append('\'').append(s.toString()).append('\'')
        }
    }

    fun newline(needed: Boolean, indent: Int, sb: StringBuffer) {
        if (!needed || sb.size == 0) return
        var nbx = sb.size - 1
        while (nbx >= 0 && sb.charAt(nbx) == ' ') nbx--
        if (sb.charAt(nbx) != '\n') sb.append('\n')
        for (i in 1..indent) sb.append(' ')
    }

    fun windowClosing() {
        prefs.setQueryText(view.getQueryText())
    }

    fun startUi() {
        view.setQueryText(prefs.getQueryText()?:"")
    }
    public fun getJdbcDrivers(): Array<NamedStringPair> {
        if (_jdbcDrivers == null) {
            val file = File("drivers.yaml")
            [suppress("CAST_NEVER_SUCCEEDS")]
            if (file.exists()) {
                val path = file.getAbsolutePath()
                _jdbcDrivers = Yaml().load(FileInputStream(file)) as MutableMap<String, Map<String, String>>
                val driverMap = _jdbcDrivers!!
                val iterator = driverMap.iterator()
                val driverArray = Array<NamedStringPair>(driverMap.size) {
                    val at = iterator.next();
                    NamedStringPair(at.key, at.value["class"]!!, at.value["jar"]!!)
                }
                driverArray.sort()
                return driverArray
            }
        }
        return arrayOfNulls<NamedStringPair>(0) as Array<NamedStringPair>
    }
    public fun putDriver(name: String, klass: String, jar: String) {
        val new = NamedStringPair(name, klass, jar)
        if (_jdbcDrivers == null)
            _jdbcDrivers = HashMap<String, Map<String, String>>()

        _jdbcDrivers!!.put(name,hashMapOf<String, String>(Pair<String, String>("class", klass), Pair("jar",jar)))
        saveJdbcDrivers()
    }

    public fun saveJdbcDrivers() {
        if (_jdbcDrivers == null) return;
        val drivers = _jdbcDrivers!!
        val yaml = Yaml()
        yaml.dump(_jdbcDrivers, FileWriter("drivers.yaml"))
    }

    public fun removeDriver(name:String){ _jdbcDrivers.remove(name)}
}