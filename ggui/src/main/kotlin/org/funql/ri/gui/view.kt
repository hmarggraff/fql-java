package org.funql.ri.gui

import javax.swing.*
import kotlin.swing.*
import java.awt.Desktop
import java.net.URI
import java.awt.event.ActionEvent

fun main(args: Array<String>): Unit {
    val v = View()
    v.theFrame.pack()
    v.theFrame.setLocationRelativeTo(null)
    v.theFrame.setVisible(true)


}
class View()
{
        val funqlEdit = JTextArea("Edit")
        val funqlResult = JTextArea("Result")

        val theFrame = frame("Funql Runner") {
            exitOnClose()

            jmenuBar = menuBar{
                menu("File") {
                    add(action("Open") { openFile(it)})
                    add(action("Save") { println("Show about window") })
                    add(action("New") { println("Show about window") })
                    add(action("Connect") { println("Show about window") })
                }
                menu("Help") {
                    add(action("Web Help"){
                        val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else throw Error("Desktop access not possible to launch web help");
                            try {
                                desktop.browse(URI("http://www.funql.org/funqlrunner/help"));
                            } catch (e: Exception) {
                                e.printStackTrace();
                            }
                        })
                }
            }
            center = JSplitPane(JSplitPane.VERTICAL_SPLIT, funqlEdit, funqlResult)
    }


    fun openFile(e: ActionEvent): Unit{
        val fc = JFileChooser()
        val ret = fc.showOpenDialog(theFrame)
        if (ret == JFileChooser.APPROVE_OPTION)
            {
                funqlEdit.setText(fc.getSelectedFile()?.getAbsolutePath())
            }
    }
}