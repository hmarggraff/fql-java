package kotlin.swing
import javax.swing.*
import java.awt.GridBagConstraints
import java.awt.Insets

fun gridBagPanel(cols : Int, init: GridBagger.() -> Unit): JPanel{
    val p = JPanel()
    val g = GridBagger(p, cols)
    g.init()
    return p
}

public class GridBagger(val target : JComponent, val cols : Int):GridBagConstraints(0,0,0,0,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, Insets(7,7,7,7), 0, 0) {
public fun a(c : JComponent) : GridBagger {
    addinternal(1, c, (0).toDouble())
    adjustcoords()
    return this
}
public fun a(spanh : Int, c : JComponent) : GridBagger {
    addinternal(spanh, c, (0).toDouble())
    adjustcoords()
    return this
}
public fun a(c : JComponent, wh : Double) : GridBagger {
    addinternal(1, c, wh)
    adjustcoords()
    return this
}
public fun a(spanh : Int, c : JComponent, wh : Double) : GridBagger {
    addinternal(spanh, c, wh)
    adjustcoords()
    return this
}
public fun a(spanh : Int, component : JComponent, wh : Double, wv : Double) : GridBagger {
    weighty = wv
    weightx = wh
    fill = (if (wh > 0) ((if (wv > 0) GridBagConstraints.BOTH else GridBagConstraints.HORIZONTAL)) else GridBagConstraints.NONE)
    gridwidth = spanh
    insets?.top = (if (gridy == 0) 7 else 0)
    insets?.left = (if (gridx == 0) 7 else 0)
    target.add(component, this)
    weightx = 0.0
    weighty = 0.0
    gridwidth = 1
    adjustcoords()
    return this
}
public fun addinternal(spanh : Int, c : JComponent, wh : Double) : GridBagger {
    weightx = wh
    fill = (if (wh > 0) GridBagConstraints.HORIZONTAL else GridBagConstraints.NONE)
    gridwidth = spanh
    insets?.top = (if (gridy == 0) 7 else 0)
    insets?.left = (if (gridx == 0) 7 else 0)
    target.add(c, this)
    weightx = 0.0
    gridwidth = 1
    return this
}
public fun a(label : String, spanh : Int, c : JComponent, wh : Double) : GridBagger {
    a(JLabel(label))
    a(spanh, c, wh)
    return this
}
public fun a(label : String?, spanh : Int, c : JComponent) : GridBagger {
    a(JLabel(label))
    a(spanh, c)
    return this
}
public fun a(label : String, c : JComponent) : GridBagger {
    a(JLabel(label))
    a(c, 1.0)
    return this
}
public fun plus(label : String, c : JComponent) : GridBagger {
    a(JLabel(label))
    a(c, 1.0)
    return this
}
public fun row() : GridBagger {
    gridx = 0
    gridy++
    gridwidth = 1
    return this
}
    
public fun row(label : String?, c : JComponent) : GridBagger {
    addinternal(1, JLabel(label), (0).toDouble())
    adjustcoords()
    a(cols - gridx, c, 1.0)
    row()
    return this
}
public fun row(c : JComponent) : GridBagger {
    addinternal(cols - gridx, c, 1.0)
    row()
    return this
}
private fun adjustcoords() : Unit {
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
public fun a(s : String?) : GridBagger {
    a(JLabel(s))
    return this
}
}
