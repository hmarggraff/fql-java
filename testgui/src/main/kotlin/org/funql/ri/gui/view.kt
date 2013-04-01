package org.funql.ri.gui

import javax.swing.*
import kotlin.swing.*
import java.awt.Desktop
import java.net.URI
import java.awt.event.ActionEvent
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.io.File
import java.util.Date

fun main(args: Array<String>): Unit {
    val v = SwingView()
    v.guiFrame.setSize(800, 600)
    v.guiFrame.setLocationRelativeTo(null)
    v.guiFrame.setVisible(true)
}
public enum class UserAnswer {yes; no; cancel
}

public trait TestRunnerView{
    fun askForSave(): UserAnswer;
    fun setQueryText(text: String);
    fun setResultText(text: String);
    fun getQueryText(): String
    fun getResultText(): String
    fun showSaveDialog(title:String): File?
    fun showOpenDialog(title:String): File?
    fun error(text: String);
    fun setTitle(text: String);
}
public class SwingView(): TestRunnerView
{
    val edQuery = JTextArea("Edit")
    val edResult = JTextArea("Result")
    val control = TestRunnerControl(this)


    val guiFrame = frame("Funql Runner") {
        exitOnClose()

        jmenuBar = menuBar{
            menu("File") {
                add(action("Open") { control.openFile() })
                add(action("Save Funql") { control.saveFile(false) })
                add(action("Save Funql As") { control.saveFile(true) })
                add(action("Save Results As") { control.saveFile(true, true) })
                add(action("Clear") { control.clear() })
                add(action("Run") { run() })
            }
            menu("Help") {
                add(action("Web Help") {
                    val desktop: Desktop = (if (Desktop.isDesktopSupported()) Desktop.getDesktop() else throw Error("Desktop access not possible to launch web help"))
                    try {
                        desktop.browse(URI("http://www.funql.org/funqlrunner/help"))
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                })
            }
            menu("Connections")  {
                add(action("Json1"){jsonDriverDialog(guiFrame)})
                add(action("Json2"){jsonDriverText(guiFrame)})

            }
        }
        center = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, edQuery, edResult)
    }

    fun run() {
        edResult.setText(Date().toString())
    }

    override fun askForSave(): UserAnswer {
        val answer = JOptionPane.showConfirmDialog(guiFrame, "Current queries have changed. Save?", "Changed", JOptionPane.YES_NO_CANCEL_OPTION)
        if (answer == JOptionPane.CANCEL_OPTION) return UserAnswer.cancel
        if (answer == JOptionPane.YES_OPTION) return UserAnswer.yes
        return UserAnswer.no
    }
    override fun showSaveDialog(title:String): File? {
        val fc = JFileChooser()
        fc.setDialogTitle(title)
        val ret = fc.showSaveDialog(guiFrame)
        if (ret == JFileChooser.APPROVE_OPTION)
        {
            return fc.getSelectedFile()
        }
        return null;
    }

    override fun setQueryText(text: String) = edQuery.setText(text)
    override fun setResultText(text: String) = edResult.setText(text)
    override fun getQueryText(): String = edQuery.getText()!!
    override fun getResultText(): String = edResult.getText()!!

    override fun showOpenDialog(title: String): File? {
        val fc = JFileChooser()
        if (fc.showOpenDialog(guiFrame) == JFileChooser.APPROVE_OPTION)
            return fc.getSelectedFile()
        return null
    }
    override fun error(text: String) = JOptionPane.showMessageDialog(guiFrame, text)
    override fun setTitle(text: String) = guiFrame.setTitle(text)

}