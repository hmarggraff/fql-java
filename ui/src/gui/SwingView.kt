package org.funql.ri.gui

import javax.swing.*
import kotlin.swing.*
import java.awt.Desktop
import java.net.URI
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.event.MenuListener
import javax.swing.event.MenuEvent
import org.funql.ri.data.FunqlConnection
import java.awt.event.KeyEvent
import java.awt.event.InputEvent
import java.net.URL
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.Insets
import java.awt.Image
import org.funql.ri.gui.swing.toolbar
import org.funql.ri.gui.swing.forms.formDialog
import org.funql.ri.gui.swing.forms.nameComponent
import org.funql.ri.gui.swing.selection
import org.funql.ri.gui.swing.button
import org.funql.ri.kotlinutil.mapextensions.toStringMap
import org.funql.ri.gui.swing.forms.formDialogStrings
import java.util.HashMap

fun main(args: Array<String>): Unit {
    val v = SwingView()
    v.guiFrame.setSize(800, 600)
    v.guiFrame.setLocationRelativeTo(null)
    v.guiFrame.setVisible(true)
}
public enum class UserAnswer {yes; no; cancel
}


public class SwingView : RunnerView {
    val edQuery = JTextArea("Edit")
    val edResult = JTextArea("Result")
    val control = RunnerControl(this)
    val closemenu = JMenu("Close");
    val runAction = action("Run", "Run the query under the cursor", null, icon("media_play_green.png")) { run() }
    val openAction = action("Open", "Open a file with queries", null, icon("folder_out.png")) { control.openFile() }
    val saveQueryAction = action("Save Queries", "Save the query text in the query editor", null, icon("disk_blue.png")) { control.saveFile(false) }
    val saveQueryAsAction = action("Save Queries As", "Save the query text in the query editor in another file", null, icon("disks.png")) { control.saveFile(true) }
    val saveResultsAsAction = action("Save Results As", "Save result output as", null, icon("document_out.png")) { control.saveFile(true, true) }
    val clearAction = action("Clear") { control.clear() }


    val guiFrame: JFrame = frame("Funql Runner")
    {
        exitOnClose()
        setIconImage(image("transform2.png"))

        jmenuBar = menuBar {
            menu("File") {
                add(openAction)
                add(previous())
                add(saveQueryAction)
                add(saveQueryAsAction)
                add(saveResultsAsAction)
                add(clearAction)
                add(runAction)
            }
            menu("Connections") {
                menu("Open") {
                    add(action("Open Json Text ...") {
                        val props = jsonDriverText(guiFrame)
                        if (props != null)
                            control.createJsonConnection(props)
                    })
                    add(action("Json File ...") {
                        val props = jsonDriverFile(guiFrame)
                        if (props != null)
                            control.createJsonConnection(props)
                    })
                    add(action("MongoDB ...") {
                        val mprops = mongoDriver(guiFrame)
                        if (mprops != null)
                            control.createMongoConnection(mprops)
                    })
                    add(action("Relational ...") {
                        val relprops: MutableMap<String, String>? = jdbcDriver(guiFrame)
                        if (relprops != null) {
                            control.createJdbcConnection(relprops)
                        }
                    })
                }
                menu("Edit") { }

                add(closemenu)
            }
            menu("Help") {
                add(action("Web Help") {
                    val desktop: Desktop? = (if (Desktop.isDesktopSupported()) Desktop.getDesktop() else throw Error("Desktop access not possible to launch web help"))
                    try {
                        desktop?.browse(URI("http://www.funql.org/funqlrunner/help"))
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                })
            }


        }
        north = toolbar() {
            add(iconbutton(openAction))
            add(iconbutton(saveQueryAction))
            add(iconbutton(saveQueryAsAction))
            add(iconbutton(saveResultsAsAction))
            add(iconbutton(runAction))
        }
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, JScrollPane(edQuery), JScrollPane(edResult))
        splitPane.setDividerLocation(400)
        center = splitPane
    };
    {
        closemenu.addMenuListener(object : MenuListener {

            public override fun menuSelected(p0: MenuEvent?) {
                closemenu.removeAll()
                control.connections.forEach {
                    closemenu.add(ConnectionCloser(it))
                }
            }
            public override fun menuDeselected(p0: MenuEvent?) {
            }
            public override fun menuCanceled(p0: MenuEvent?) {
            }
        })
        defineShortcutkey(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK), action("Search") { showsearch() })
        defineShortcutkey(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK), openAction)
        defineShortcutkey(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK), saveQueryAction)
        defineShortcutkey(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK or InputEvent.SHIFT_MASK), saveQueryAsAction)
        defineShortcutkey(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK), runAction)
        guiFrame.addWindowListener(object : WindowAdapter() {


            override fun windowClosing(e: WindowEvent) {
                control.windowClosing()
            }
        })

        control.startUi()
    }


    fun iconbutton(action: Action): JButton {
        val ret = JButton(action)
        ret.setBorder(null)
        ret.setMargin(Insets(1, 1, 1, 1))
        ret.setHideActionText(true)
        ret.setOpaque(false);
        ret.setContentAreaFilled(false);
        ret.setBorderPainted(false);
        return ret
    }


    public fun defineShortcutkey(key: KeyStroke?, action: Action?): Unit {
        var im: InputMap? = guiFrame.getRootPane()?.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        im?.put(key, action)
        var am: ActionMap? = guiFrame.getRootPane()?.getActionMap()
        am?.put(action, action)
    }

    fun showsearch() {
    }


    fun previous(): JMenu {
        val connections = org.funql.ri.gui.prefs.getConnections()
        val ret = JMenu("Reopen ..")
        connections.forEach {
            ret.add(Reopener(it))
        }
        return ret
    }

    fun run() {
        var sel: String? = edQuery.getSelectedText()
        val q = if (sel == null || sel?.length == 0) {
            val query: String = edQuery.getText()!!
            val pos = edQuery.getCaretPosition()
            val start = query.lastIndexOf("\n\n", pos)
            val end = query.indexOf("\n\n", pos)
            query.substring(if (start >= 0) start + 2 else 0, if (end >= 0) end else query.length)
        } else
            sel
        control.run(q!!)
    }

    public fun jsonDriverText(owner: JFrame): MutableMap<String, String>? {
        val values = formDialogStrings(owner, "Use Json Text for a Connection", 2) {
            a("Connection Name", nonEmpty(JTextField(), GuiKeys.nameKey))
            a("Json Text")
            a(1, nameComponent(JTextArea(), GuiKeys.textKey), 1.0, 1.0)
        }
        return values;
    }
    public fun jsonDriverFile(owner: JFrame): MutableMap<String, String>? {
        val values = formDialogStrings(owner, "Use Json Text for a Connection", 3) {
            a("Connection Name", 2, nonEmpty(JTextField(), GuiKeys.nameKey), 1.0)
            val edname = nonEmpty(JTextField(), GuiKeys.fileKey)
            a("Json File", edname, 1.0)
            a(button("..") {
                val f = showOpenDialog("Please select a Json or Yaml File")
                if (f != null)
                    edname.setText(f.getAbsolutePath())
            })
        }
        return values;
    }

    public fun mongoDriver(owner: JFrame): MutableMap<String, String>? {
        val values = formDialogStrings(owner, "Connect to a MongoDB", 2) {
            a("Connection Name", 2, nonEmpty(JTextField(), GuiKeys.nameKey), 1.0)
            a("Database Name", nonEmpty(JTextField(), GuiKeys.dbKey), 1.0)
            a("Host Name", nameComponent(JTextField(), GuiKeys.hostKey), 1.0)
            a("Port Number", nameComponent(JTextField(), GuiKeys.portKey), 1.0)
        }
        return values;
    }
    public fun jdbcDriver(owner: JFrame): MutableMap<String, String>? {
        val comboBoxModel = DefaultComboBoxModel<Map<String, String>>(control.getJdbcDrivers())

        val from = formDialog(owner, "Connect to a Relational Database", 3) {
            row("Connection Name", nonEmpty(JTextField(), GuiKeys.nameKey))
            a("Driver", selection(comboBoxModel), GuiKeys.infoKey, 1.0)
            a(button(icon("add.png"), "Add a jar file with a Jdbc Driver") {
                val jdbcDriverInfo = addJdbcDriver(owner)
                if (jdbcDriverInfo != null) comboBoxModel.addElement(jdbcDriverInfo)
                comboBoxModel.setSelectedItem(jdbcDriverInfo)
            })
            row("Connection URL", nonEmpty(JTextField(), GuiKeys.connKey))
            row("User name", nameComponent(JTextField(), GuiKeys.userKey))
            row("Password", nameComponent(JTextField(), GuiKeys.passwdKey))
        }
        if (from != null) {
            val ret: MutableMap<String, String> = HashMap<String, String>()
            from.entrySet().forEach {
                val value = it.value
                if (value is String) ret.put(it.getKey(), value)
            }
            val driverInfo = from[GuiKeys.infoKey] as Map<String, String>
            ret.putAll(driverInfo)
            return ret
        } else return null
    }

    public fun addJdbcDriver(owner: JFrame): Map<String, String>? {
        val fileField = JTextField()
        val values = formDialogStrings(owner, "Load a Jdbc Driver", 3) {
            row("Driver Name", nonEmpty(JTextField(), GuiKeys.driverKey))
            row("Driver Class", nonEmpty(JTextField(), GuiKeys.driverClassKey))
            a("Jar File", nameComponent(fileField, GuiKeys.fileKey), 1.0)
            a(button(icon("folder_out.png"), "Select jar file") {
                val file: File? = showOpenDialog("Jar File")
                fileField.setText(file?.getAbsolutePath())
            })
        }
        return values
    }

    override fun askForSave(): UserAnswer {
        val answer = JOptionPane.showConfirmDialog(guiFrame, "Current queries have changed. Save?", "Changed", JOptionPane.YES_NO_CANCEL_OPTION)
        if (answer == JOptionPane.CANCEL_OPTION) return UserAnswer.cancel
        if (answer == JOptionPane.YES_OPTION) return UserAnswer.yes
        return UserAnswer.no
    }
    override fun showSaveDialog(title: String): File? {
        val fc = JFileChooser()
        fc.setDialogTitle(title)
        val ret = fc.showSaveDialog(guiFrame)
        if (ret == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile()
        }
        return null;
    }

    protected fun icon(name: String): Icon {
        try {
            val u: URL? = javaClass<SwingView>().getResource("icons/" + name)
            if (u == null) {
                System.out.println("Icon " + name + " not found.")
                return javax.swing.plaf.metal.MetalIconFactory.getFileChooserHomeFolderIcon()!!
            } else
                return ImageIcon(u)
        } catch (ex: Exception) {
            System.out.println("Icon " + name + " could not be read.")
            return javax.swing.plaf.metal.MetalIconFactory.getFileChooserHomeFolderIcon()!!

        }

    }

    protected fun image(name: String): Image? {
        try {
            val u: URL? = javaClass<SwingView>().getResource("icons/" + name)
            if (u != null) return ImageIcon(u).getImage()
        } catch (ex: Exception) {
            System.out.println("Icon " + name + " could not be read.")
        }
        return null
    }


    override fun setQueryText(text: String) = edQuery.setText(text)
    override fun setResultText(text: String) = edResult.setText(text)
    override fun getQueryText(): String = edQuery.getText()!!
    override fun getResultText(): String = edResult.getText()!!

    override fun showOpenDialog(title: String): File? {
        val fc = JFileChooser()
        if (fc.showOpenDialog(guiFrame) == JFileChooser.APPROVE_OPTION)
            return fc.getSelectedFile()
        return null
    }
    override fun error(text: String) = JOptionPane.showMessageDialog(guiFrame, text)
    override fun setTitle(text: String) = guiFrame.setTitle(text)
    inner class ConnectionCloser(val conn: FunqlConnection) : AbstractAction(conn.getName()) {


        override fun actionPerformed(e: ActionEvent) {
            control.close(conn)
        }
    }
    inner class Reopener(val conn: MutableMap<String, String>) : AbstractAction(conn[GuiKeys.connKey]!!) {

        override fun actionPerformed(e: ActionEvent) {
            control.createConnection(conn)
        }
    }
}

