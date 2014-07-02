package org.funql.ri.gui.swing

import javax.swing.*
import java.awt.event.*
import java.awt.*


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

public fun toolbar(init: JToolBar.() -> Unit): JToolBar {
    val answer = JToolBar()
    answer.setFloatable(false)
    answer.setLayout(FlowLayout(FlowLayout.LEFT, 5, 0))

    answer.init()
    return answer;
}

fun JToolBar.toolbarButton(icon: Icon, toolTip: String? = null, action: Action): JButton {
    val answer = JButton(icon)
    if (toolTip != null) answer.setToolTipText(toolTip)
    answer.setAction(action)
    add(answer)
    return answer
}

fun JToolBar.button(icon: Icon, toolTip: String? = null, fn: (ActionEvent) -> Unit): JButton {
    val answer = JButton(icon)
    if (toolTip != null) answer.setToolTipText(toolTip)

    val action = object: AbstractAction() {
        public override fun actionPerformed(p0: ActionEvent) {
            fn(p0)
        }
    }
    answer.setAction(action)
    add(answer)
    return answer
}
