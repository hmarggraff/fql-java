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
import org.funql.ri.util.Keys
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager
import org.funql.ri.classloading.JarClassLoader
import org.funql.ri.gui.swing.enabledBy
import org.funql.ri.gui.swing.forms.Validator
import java.awt.FlowLayout
import org.funql.ri.gui.swing.RadioPanel

fun main(args: Array<String>): Unit {
    val v = SwingView()
    v.guiFrame.setSize(800, 600)
    v.guiFrame.setLocationRelativeTo(null)
    v.guiFrame.setVisible(true)
}
public enum class UserAnswer {yes; no; cancel
}

val log = LogManager.getLogger("runner.view")!!
val startup = MarkerManager.getMarker("funqlrunner.startup")!!


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
                    add(action("Open Json Text ...") { jsonConnectionInlineText(guiFrame) })
                    add(action("Json File ...") { jsonConnectionDatafile(guiFrame) })
                    add(action("MongoDB ...") { mongoConnection(guiFrame) })
                    add(action("Relational ...") { jdbcConnection(guiFrame) })
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

    public fun jsonConnectionInlineText(owner: JFrame) {
        val values = formDialogStrings(owner, "Use Json Text for a Connection", 2) {
            a("Connection Name", nonEmpty(JTextField(), Keys.name))
            a("Json Text")
            a(1, nameComponent(JTextArea(), Keys.text), 1.0, 1.0)
        }
        openConnectionChecked(values, DriverTypes.json)
    }
    public fun jsonConnectionDatafile(owner: JFrame) {
        val values = formDialogStrings(owner, "Use Json Text for a Connection", 3) {
            a("Connection Name", 2, nonEmpty(JTextField(), Keys.name), 1.0)
            val edname = nonEmpty(JTextField(), Keys.file)
            a("Json File", edname, 1.0)
            a(button("..") {
                val f = showOpenDialog("Please select a Json or Yaml File")
                if (f != null)
                    edname.setText(f.getAbsolutePath())
            })
        }
        openConnectionChecked(values, DriverTypes.json)
    }

    public fun mongoConnection(owner: JFrame) {
        val values = formDialogStrings(owner, "Connect to a MongoDB", 2) {
            a("Connection Name", 2, nonEmpty(JTextField(), Keys.name), 1.0)
            a("Database Name", nonEmpty(JTextField(), Keys.db), 1.0)
            a("Host Name", nameComponent(JTextField(), Keys.host), 1.0)
            a("Port Number", nameComponent(JTextField(), Keys.port), 1.0)
        }
        openConnectionChecked(values, DriverTypes.mongo)
    }

    public fun jdbcConnection(owner: JFrame){
        val comboBoxModel = DefaultComboBoxModel<Map<String, String>>(control.getJdbcDrivers())

        val from = formDialog(owner, "Connect to a Relational Database", 3) {
            row("Connection Name", nonEmpty(JTextField(), Keys.name))
            a("Driver", selection(comboBoxModel, ComboBoxRenderer4Map(Keys.driver)), Keys.info, 1.0)
            a(button(icon("add.png"), "Add a jar file with a Jdbc Driver") {
                val jdbcDriverInfo = addJdbcDriver(owner)
                if (jdbcDriverInfo != null) {
                    control.putDriver(jdbcDriverInfo)
                    comboBoxModel.addElement(jdbcDriverInfo)
                    comboBoxModel.setSelectedItem(jdbcDriverInfo)
                }
            })
            row("Connection URL", nonEmpty(JTextField(), Keys.connection))
            row("User name", nameComponent(JTextField(), Keys.user))
            row("Password", nameComponent(JTextField(), Keys.passwd))
            row("Execution", RadioPanel(Keys.execution, Keys.local to "Local",Keys.server to "Server"))
        }
        if (from == null) return
        val ret = HashMap<String, String>()
        from.entrySet().forEach {
            val value = it.value
            if (value is String) ret.put(it.getKey(), value)
        }
        val driverInfo = from[Keys.info] as Map<String, String>
        ret.putAll(driverInfo)
        openConnectionChecked(ret, DriverTypes.relational)
    }

    public fun addJdbcDriver(owner: JFrame): Map<String, String>? {
        val fileField = JTextField()
        val classField = JTextField()
        val values = formDialogStrings(owner, "Load a Jdbc Driver", 3) {
            row("Driver Name", nonEmpty(JTextField(), Keys.driver))
            row("Driver Class", nonEmpty(classField, Keys.klass))
            a("Jar File", nonEmpty(fileField, Keys.file), 1.0)
            a(button(icon("folder_out.png"), "Select jar file") {
                val file: File? = showOpenDialog("Jar File")
                fileField.setText(file?.getAbsolutePath())
            })
            var isChecked: Boolean = false
            val doCheck = button("Check") {
                val className = classField.getText()!!
                val fileName = fileField.getText()!!
                try {
                    val driverClass = JarClassLoader.loadClassFromJar(className, fileName)
                    val driverInterface = javaClass<java.sql.Driver>()
                    isChecked = driverInterface.isAssignableFrom(driverClass)
                    if (isChecked) {
                        JOptionPane.showMessageDialog(owner, "Class $className loaded. You can now use the driver in your connection spec.")
                        validateAll()
                    } else
                        JOptionPane.showMessageDialog(owner, "Class $className is not a java.sql.Driver. Please check your class name and jar file.")

                } catch(ex: ClassNotFoundException) {
                    JOptionPane.showMessageDialog(owner, "Cannot load $className from $fileName")
                }
            }
            doCheck.enabledBy(fileField, classField)
            addButton(doCheck)
            addValidatorForOk {
                isChecked
            }
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
                log.warn(startup, "Icon " + name + " not found.")
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
    fun openConnectionChecked(conn: MutableMap<String, String>?, drivertype: String) {
        if (conn == null) return
        conn[Keys.typ] = drivertype
        openTypedConnectionChecked(conn)
    }

    private fun openTypedConnectionChecked(conn: MutableMap<String, String>) {
        try {
            control.createConnection(conn)
        } catch (x: Throwable) {
            JOptionPane.showMessageDialog(guiFrame, "Opening connection ${conn[Keys.name]} failed: ${x.getMessage()}")
        }
    }

    inner class Reopener(val conn: MutableMap<String, String>) : AbstractAction(conn[Keys.name]!!) {

        override fun actionPerformed(e: ActionEvent) {
            openTypedConnectionChecked(conn)
        }
    }
}

