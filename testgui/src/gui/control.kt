package org.funql.ri.gui

import java.io.File
import java.io.FileWriter
import java.io.FileReader
import org.funql.ri.data.FunqlConnection
import org.funql.ri.jsondriver.JsonConnection
import java.util.ArrayList
import org.funql.ri.mongodriver.FunqlMongoConnection
import org.funql.ri.parser.FqlParser
import org.yaml.snakeyaml.Yaml
import java.io.StringWriter

class TestRunnerControl(val view: TestRunnerView) {

    public final val conNameKey: String = "conName"
    public final val textKey: String = "text"
    public final val fileKey: String = "file"
    public final val dbKey: String = "db"
    public final val hostKey: String = "host"
    public final val portKey: String = "port"

    var textChanged = false;
    var funqlFile: File? = null;
    val connections = ArrayList<FunqlConnection>()

    fun writeFile(file: File, text: String) {
        var w: FileWriter? = null
        try
        {
            w = FileWriter(file)
            w?.write(text)
            textChanged = false
        }
        finally {
            w?.close()
        }
    }


    fun saveFile(force: Boolean, result: Boolean = false): Unit {
        if (funqlFile == null || force || result)
        {
            val file = view.showSaveDialog(if (result) "Save Results" else "Save Query")
            if (file != null)
                writeFile(file, if (result) view.getResultText() else view.getQueryText())
        }
        else
            writeFile(funqlFile!!, if (result) view.getResultText() else view.getQueryText())
    }

    fun saveChanged(): Boolean {
        if (textChanged){
            val userAnswer = view.askForSave()
            if (userAnswer == UserAnswer.cancel) return true
            if (userAnswer == UserAnswer.yes) saveFile(false)
        }
        return false;
    }

    fun clear(): Unit {
        if (saveChanged()) return
        funqlFile = null
        view.setQueryText("")
    }

    fun openFile(): Unit {
        if (saveChanged()) return
        val file = view.showOpenDialog("Open query file")
        if (file != null && file.canRead())
        {
            val r = FileReader(file)
            val s = r.readText()

            view.setQueryText(s)
            r.close()
            funqlFile = file;
            view.setTitle(funqlFile!!.getPath())
        }
        else
            view.error("File ${file} could not be opened.")
    }
    public fun createJsonConnection(props: Map<String, String>): Unit
    {
        val ret = JsonConnection(props[conNameKey]!!, props)
        connections.add(ret)
    }
    public fun createMongoConnection(props: Map<String, String>): Unit
    {
        val ret = FunqlMongoConnection(props[conNameKey]!!, props)
        connections.add(ret)
    }

    public fun run(q: String){
        try {

            val fqlIterator = FqlParser.runQuery(q,null, connections)

            if (fqlIterator == null) return
            val y = Yaml()
            val ret = StringWriter()
            while (fqlIterator.hasNext()) {
                val v = fqlIterator.next()!!
                ret.append("- ")
                y.dump(v, ret)
            }
            view setResultText ret.toString()
        } catch (r:Throwable) {
            view.error(r.getMessage()!!)
        }

    }
    public fun close(conn: FunqlConnection){
        conn.close()
        connections.remove(conn)
    }



}