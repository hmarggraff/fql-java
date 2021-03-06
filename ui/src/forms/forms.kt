package org.funql.ri.gui.swing.forms

import javax.swing.JComponent
import java.util.HashMap
import javax.swing.JPanel
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.GridBagConstraints
import javax.swing.JLabel
import javax.swing.JDialog
import java.awt.Frame
import javax.swing.BorderFactory
import javax.swing.border.BevelBorder
import kotlin.swing.*
import javax.swing.text.JTextComponent
import javax.swing.JComboBox
import javax.swing.JList
import javax.swing.event.DocumentListener
import javax.swing.event.DocumentEvent
import javax.swing.JButton
import javax.swing.JTree
import java.util.ArrayList
import org.funql.ri.gui.swing.dialog
import org.funql.ri.util.KotlinWorkarounds
import java.awt.Color
import org.funql.ri.kotlinutil.mapextensions.toStringMap
import java.awt.Button
import javax.swing.Icon
import java.awt.event.ActionEvent
import org.funql.ri.gui.swing.RadioPanel


fun form(cols: Int, init: FormBuilder.() -> Unit): FormBuilder {
    val p = JPanel(GridBagLayout())
    val g = FormBuilder(p, cols)
    g.init()
    return g
}

fun formDialog(owner: Frame?, title: String, cols: Int, init: FormBuilder.() -> Unit): MutableMap<String, Any>? {
    val p = JPanel(GridBagLayout())
    val g = FormBuilder(p, cols)
    g.init()
    g.row()

    val dialog: JDialog = dialog(owner, title) {
        center = p
        setSize(600, 450)
        setLocationRelativeTo(owner)
        setModal(true)
    }

    g.okButton.addActionListener {
        g.ok = true; dialog.setVisible(false);dialog.dispose()
    }
    val buttons = panel{
        g.extraButtons.forEach { add(it) }
        add(g.okButton)
        add(button("Cancel", { dialog.setVisible(false);dialog.dispose() }))
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED))
    }
    dialog.south = buttons
    dialog.setVisible(true)
    return if (g.ok) g.getResults() else null
}

fun formDialogStrings(owner: Frame?, title: String, cols: Int, init: FormBuilder.() -> Unit): MutableMap<String, String>? = toStringMap(formDialog(owner,title,cols,init))

fun nameComponent(c: JComponent, nam: String): JComponent {
    c.setName(nam)
    return c
}

public open class FormBuilder(val target: JComponent, val cols: Int): GridBagConstraints(0, 0, 0, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(7, 7, 7, 7), 0, 0)
{
    val values = HashMap<String, JComponent>()
    var ok: Boolean = false
    val okButton = JButton("Ok")
    val validators = ArrayList<Validator>()
    val extraButtons = ArrayList<JButton>()

    public fun a(c: JComponent) {
        record(c)
        addinternal(1, c, 0.0)
        adjustcoords()
    }
    public fun a(spanh: Int, c: JComponent) {
        record(c)
        addinternal(spanh, c, 0.0)
        adjustcoords()
    }
    public fun a(c: JComponent, wh: Double) {
        record(c)
        addinternal(1, c, wh)
        adjustcoords()
    }
    public fun a(spanh: Int, c: JComponent, wh: Double) {
        record(c)
        addinternal(spanh, c, wh)
        adjustcoords()
    }

    public fun a(spanh: Int, c: JComponent, wh: Double, wv: Double) {
        record(c)
        weighty = wv
        fill = GridBagConstraints.BOTH
        addinternal(spanh, c, wh)
        weighty = 0.0
        adjustcoords()
    }


    public fun row(c: JComponent) {
        record(c)
        addinternal(cols - gridx, c, 1.0)
        row()
    }

    public fun a(c: JComponent, key: String) {
        values[key] = c
        addinternal(1, c, 0.0)
        adjustcoords()
    }
    public fun a(spanh: Int, c: JComponent, key: String) {
        values[key] = c
        addinternal(spanh, c, 0.0)
        adjustcoords()
    }
    public fun a(c: JComponent, wh: Double, key: String) {
        values[key] = c
        addinternal(1, c, wh)
        adjustcoords()
    }
    public fun a(spanh: Int,
                 c: JComponent,
                 key: String,
                 wh: Double = 1.0) {
        values[key] = c
        addinternal(spanh, c, wh)
        adjustcoords()
    }

    public fun row(c: JComponent, key: String) {
        values[key] = c
        addinternal(cols - gridx, c, 1.0)
        row()
    }

    public fun a(spanh: Int, c: JComponent, wh: Double, wv: Double, key: String) {
        values[key] = c
        weighty = wv
        addinternal(spanh, c, wh)
        weighty = 0.0
        adjustcoords()
    }

    public fun a(label: String, spanh: Int, c: JComponent, wh: Double) {
        a(JLabel(label))
        a(spanh, c, wh)
    }
    public fun a(label: String, c: JComponent, wh: Double) {
        a(JLabel(label))
        a(1, c, wh)
    }
    public fun a(label: String, c: JComponent, key: String, wh: Double) {
        values[key] = c
        a(JLabel(label))
        a(1, c, wh)
    }
    public fun a(label: String, spanh: Int, c: JComponent) {
        a(JLabel(label))
        a(spanh, c)
    }
    public fun a(label: String, c: JComponent) {
        a(JLabel(label))
        a(c, 1.0)
    }

    public fun row(label: String, c: JComponent) {
        addinternal(1, JLabel(label), 0.0)
        adjustcoords()
        a(cols - gridx, c, 1.0)
        row()
    }
    public fun row(label: String, c: JComponent, key: String) {
        values[key] = c
        addinternal(1, JLabel(label), 0.0)
        adjustcoords()
        a(cols - gridx, c, 1.0)
        row()
    }

    public fun row() {
        gridwidth = 1
        weightx = 0.0
        if (gridx == 0)
            return
        gridx = 0
        gridy++
    }

    private fun adjustcoords(): Unit {
        if (gridx < cols - gridwidth)
        {
            gridx += gridwidth
        }
        else
        {
            gridx = 0
            gridy++
        }
    }

    public fun a(s: String) {
        a(JLabel(s))
    }


    fun JComponent.extract(): Any? {
        val v: Any? = when (this){
            is JTextComponent -> getText()
            is JComboBox<*> -> getSelectedItem()
            is JList<*> -> getSelectedValue()
            is JTree -> getSelectionModel()?.getSelectionPath()?.getLastPathComponent()
            is RadioPanel -> getSelected()
            else -> null
        }
        return v
    }

    public fun getResults(): MutableMap<String, Any> {
        val ret = HashMap<String, Any>()
        values.entrySet().forEach{
            val value = it.value.extract()
            if (value != null) ret.put(it.getKey(), value)
        }
        return ret
    }

    public fun nonEmpty(c: JTextComponent, name: String): JTextComponent {
        c.setName(name)
        return nonEmpty(c)
    }

    public fun nonEmpty(c: JTextComponent): JTextComponent {
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.blue),c.getBorder()))
        okButton.setEnabled(c.getDocument()!!.getLength() > 0)
        c.getDocument()!!.addDocumentListener(validateDocumentChangeListener)
        validators.add(NonEmptyTextValidator(c))
        return c
    }


    protected fun addinternal(spanh: Int, c: JComponent, wh: Double) {
        weightx = wh
        gridwidth = spanh
        fill = (if (wh > 0) (if (weighty > 0) GridBagConstraints.BOTH else GridBagConstraints.HORIZONTAL) else GridBagConstraints.NONE)
        insets = Insets(if (gridy == 0) 7 else 0, if (gridx == 0) 7 else 0, 7, 7)
        target.add(c, this)
    }

    protected fun record(c: JComponent)
    {
        val s: String? = KotlinWorkarounds.getComponentName(c)
        if (s != null)
            values[s] = c
    }

    public fun validateAll() { okButton.setEnabled(validators.all { it.valid() }) }

    public fun addValidatorForOk(v: ()->Boolean){ validators.add(object:Validator{ override fun valid(): Boolean = v() }) }

    val validateDocumentChangeListener = object : DocumentListener{

        public override fun insertUpdate(p0: DocumentEvent) {
            validateAll()
        }
        public override fun removeUpdate(p0: DocumentEvent) {
            validateAll()
        }
        public override fun changedUpdate(p0: DocumentEvent) {
            validateAll()
        }
    }
    public fun addButton(text: String, icon: Icon? = null, action : (ActionEvent)-> Unit){
        val extraButton = button(text, action)
        extraButton.setIcon(icon)
        extraButtons.add(0,extraButton)
    }
    public fun addButton(b: JButton){ extraButtons.add(b) }
}


trait Validator
{
    public fun valid(): Boolean
}

class NonEmptyTextValidator(val c: JTextComponent): Validator {
    public override fun valid(): Boolean {
        return c.getDocument()!!.getLength() > 0
    }
}
