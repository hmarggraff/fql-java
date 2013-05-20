package kotlin.swing

import javax.swing.*
import java.awt.event.*
import java.awt.*


fun frame(title : String, init : JFrame.() -> Unit) : JFrame {
  val result = JFrame(title)
  result.init()
  return result
}

fun panel(init: JPanel.() -> Unit): JPanel {
    val p = JPanel()
    p.init()
    return p
}

fun dialog(owner: Frame?, title : String, init : JDialog.() -> Unit) : JDialog {
  val result = JDialog(owner,title)
  result.init()
  return result
}
fun dialog(owner: Dialog?, title : String, init : JDialog.() -> Unit) : JDialog {
  val result = JDialog(owner,title)
  result.init()
  return result
}
