package kotlin.swing

import javax.swing.*
import java.awt.Frame

fun inputPanel(owner: Frame, title : String, init : JPanel.() -> Unit) : Unit {
    val panel = JPanel()
    panel.init()
    JOptionPane.showInputDialog(owner, panel, title, JOptionPane.OK_CANCEL_OPTION)

    //return result
}
