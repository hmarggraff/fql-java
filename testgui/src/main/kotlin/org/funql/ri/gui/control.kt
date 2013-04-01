package org.funql.ri.gui

import java.io.File
import java.io.FileWriter
import java.io.FileReader

class TestRunnerControl(val view: TestRunnerView) {
    var textChanged = false;
    var funqlFile: File? = null;

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

    fun saveChanged() : Boolean{
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
    }