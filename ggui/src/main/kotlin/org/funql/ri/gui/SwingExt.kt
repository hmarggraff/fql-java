package org.funql.ri.gui

import javax.swing.JFileChooser
import java.io.FileReader
import java.awt.event.ActionEvent
import java.awt.Component
import java.io.File

fun JFileChooser.selectAndReadFile(parent: Component): String? {
        val file = JFileChooser.selectFileDialog(parent)
        if (file != null)
        {
            val r = FileReader(file)
            val s = r.readText()
            r.close()
        }
    return null;
}

fun JFileChooser.selectFileDialog(parent: Component):File?
{
    val fc = JFileChooser()
    val ret = fc.showOpenDialog(parent)
    if (ret == JFileChooser.APPROVE_OPTION) return fc.getSelectedFile()
    return null;

}
