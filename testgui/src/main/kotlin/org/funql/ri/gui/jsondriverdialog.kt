package org.funql.ri.gui

import kotlin.swing.*
import javax.swing.JFrame
import javax.swing.JTextArea
import javax.swing.JTextField
import org.funql.ri.data.FunqlDriver
import org.funql.ri.data.FunqlConnection
import javax.swing.JOptionPane

public fun jsonDriverDialog(owner: JFrame): Unit {
    //FunqlConnection? {
    val dialog = dialog("Open Json Connection", owner) {
        val panel = gridBagPanel(2) {
            a("Name", JTextField())
            a("Text", JTextArea())
        }
        setContentPane(panel);
    }
    dialog.setSize(600, 400)
    dialog.setVisible(true)
}

public fun jsonDriverText(owner: JFrame): Unit {
    //FunqlConnection? {
    val panel = gridBagPanel(2) {
        a("Name", JTextField())
        a("Text", JTextArea())
    }
    JOptionPane.showInputDialog(owner, panel, "Open Json Connection", JOptionPane.OK_CANCEL_OPTION)
}