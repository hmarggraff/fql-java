package kotlin.swing

import javax.swing.*
import java.awt.GridBagConstraints
import java.awt.Insets
import java.awt.GridBagLayout

fun gridBagPanel(cols: Int, init: GridBagBuilder.() -> Unit): JPanel {
    val p = JPanel(GridBagLayout())
    val g = GridBagBuilder(p, cols)
    g.init()
    return p
}


public open class GridBagBuilder(val target: JComponent, val cols: Int): GridBagConstraints(0, 0, 0, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(7, 7, 7, 7), 0, 0) {


    public fun a(spanh: Int, component: JComponent, wh: Double, wv: Double) {
        gridwidth = spanh
        weighty = wv
        weightx = wh
        fill = if (wh > 0) ((if (wv > 0) GridBagConstraints.BOTH else GridBagConstraints.HORIZONTAL)) else GridBagConstraints.NONE
        insets = Insets(if (gridy == 0) 7 else 0, if (gridx == 0) 7 else 0, 7, 7)
        target.add(component, this)
        weighty = 0.0
        adjustcoords()
    }


    public fun a(c: JComponent) {
        addinternal(1, c, 0.0)
        adjustcoords()
    }
    public fun a(spanh: Int, c: JComponent) {
        addinternal(spanh, c, 0.0)
        adjustcoords()
    }
    public fun a(c: JComponent, wh: Double) {
        addinternal(1, c, wh)
        adjustcoords()
    }
    public fun a(spanh: Int, c: JComponent, wh: Double) {
        addinternal(spanh, c, wh)
        adjustcoords()
    }

    public fun a(label: String, spanh: Int, c: JComponent, wh: Double) {
        a(JLabel(label))
        a(spanh, c, wh)
    }
    public fun a(label: String, spanh: Int, c: JComponent) {
        a(JLabel(label))
        a(spanh, c)
    }
    public fun a(label: String, c: JComponent) {
        a(JLabel(label))
        a(c, 1.0)
    }

    public fun row() {
        gridx = 0
        gridy++
        gridwidth = 1
        weightx = 0.0
    }

    public fun row(label: String, c: JComponent) {
        addinternal(1, JLabel(label), 0.0)
        adjustcoords()
        a(cols - gridx, c, 1.0)
        row()
    }
    public fun row(c: JComponent) {
        addinternal(cols - gridx, c, 1.0)
        row()
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


    protected fun addinternal(spanh: Int, c: JComponent, wh: Double) {
        weightx = wh
        fill = (if (wh > 0) GridBagConstraints.HORIZONTAL else GridBagConstraints.NONE)
        gridwidth = spanh
        insets = Insets(if (gridy == 0) 7 else 0, if (gridx == 0) 7 else 0, 7, 7)
        //val fillTxt = if (fill==GridBagConstraints.HORIZONTAL) "H" else "N"
        //println("($gridx: $weightx, $gridy: $weighty), fill = $fillTxt")
        target.add(c, this)
    }

}