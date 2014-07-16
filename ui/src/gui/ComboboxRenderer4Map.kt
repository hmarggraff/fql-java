package org.funql.ri.gui

import javax.swing.JLabel
import javax.swing.ListCellRenderer
import java.awt.Component
import javax.swing.JList
import javax.swing.JComponent
import java.awt.Label
import org.apache.logging.log4j.LogManager

class ComboBoxRenderer4Map(val key: String) : ListCellRenderer<Map<String, String>>, JLabel() {

    {
        setOpaque(true);
        //setHorizontalAlignment(Label.CENTER);
        //setVerticalAlignment(CENTER);
    }

    /*
     * This method finds the image and text corresponding
     * to the selected value and returns the label, set up
     * to display the text and image.
     */


    override fun getListCellRendererComponent(list: JList<out Map<String, String>>, value: Map<String, String>?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        //Get the selected index. (The index param isn't
        //always valid, so just use the value.)

        if (isSelected) {
            setBackground(list.getSelectionBackground()!!);
            setForeground(list.getSelectionForeground()!!);
        } else {
            setBackground(list.getBackground()!!);
            setForeground(list.getForeground()!!);
        }
        if (value != null && !value.containsKey(key))
        {
            val log = LogManager.getLogger("runner.view")!!
            log.error("Cannot render Combobox label. Missing value for key: $key")
        }

        setText(if (value != null) value[key] else null)

        return this;
    }
}