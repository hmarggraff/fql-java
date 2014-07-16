package org.funql.ri.gui.swing

import javax.swing.*
import java.awt.event.*
import java.awt.*


fun dialog(owner: Frame?, title: String, init: JDialog.() -> Unit): JDialog {
    val ret = JDialog(owner, title)
    ret.init()
    return ret
}
fun dialog(owner: Dialog?, title: String, init: JDialog.() -> Unit): JDialog {
    val ret = JDialog(owner, title)
    ret.init()
    return ret
}

public fun toolbar(init: JToolBar.() -> Unit): JToolBar {
    val ret = JToolBar()
    ret.setFloatable(false)
    ret.setLayout(FlowLayout(FlowLayout.LEFT, 5, 0))

    ret.init()
    return ret;
}

fun JToolBar.toolbarButton(icon: Icon, toolTip: String? = null, action: Action): JButton {
    val ret = JButton(icon)
    if (toolTip != null) ret.setToolTipText(toolTip)
    ret.setAction(action)
    add(ret)
    return ret
}

fun JToolBar.button(icon: Icon, toolTip: String? = null, fn: (ActionEvent) -> Unit): JButton {
    val ret = JButton(icon)
    if (toolTip != null) ret.setToolTipText(toolTip)

    val action = object : AbstractAction() {
        public override fun actionPerformed(p0: ActionEvent) {
            fn(p0)
        }
    }
    ret.setAction(action)
    add(ret)
    return ret
}

public fun selection(values: Array<out Any>):JComboBox<Any>{
    val combo = JComboBox(values)
    return combo;
}
public fun selection<T>(model: ComboBoxModel<T>, renderer: ListCellRenderer<T>? = null):JComboBox<T>{
    val combo = JComboBox<T>(model)
    if (renderer != null) combo.setRenderer(renderer)
    return combo;
}



fun button(icon: Icon, toolTip: String? = null, fn: (ActionEvent) -> Unit): JButton {

    val action = object : AbstractAction() {
        public override fun actionPerformed(p0: ActionEvent) {
            fn(p0)
        }
    }
    val ret = JButton(action)
    ret.setIcon(icon)
    ret.setOpaque(false);
    ret.setContentAreaFilled(false);
    ret.setBorderPainted(false);
    if (toolTip != null) ret.setToolTipText(toolTip)
    ret.setBorder(null)
    ret.setMargin(Insets(1, 1, 1, 1))


    return ret
}


