package org.funql.ri.gui

import javax.swing.*
import kotlin.swing.*
import kotlin.forms.formDialog
import kotlin.forms.name
import java.awt.Desktop
import java.net.URI
import java.awt.event.ActionEvent
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.io.File
import java.util.Date
import javax.swing.event.MenuListener
import javax.swing.event.MenuEvent
import org.funql.ri.data.FunqlConnection
import java.awt.event.ActionListener
import java.util.HashMap
import org.funql.ri.gui.SwingView.Reopener
import java.awt.event.KeyEvent
import java.awt.event.InputEvent
import java.net.URL
import java.awt.event.WindowListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.Insets

fun main(args: Array<String>): Unit {
    val v = SwingView()
    v.guiFrame.setSize(800, 600)
    v.guiFrame.setLocationRelativeTo(null)
    v.guiFrame.setVisible(true)
}
public enum class UserAnswer {yes; no; cancel
}

public trait TestRunnerView{
    fun askForSave(): UserAnswer;
    fun setQueryText(text: String);
    fun setResultText(text: String);
    fun getQueryText(): String
    fun getResultText(): String
    fun showSaveDialog(title: String): File?
    fun showOpenDialog(title: String): File?
    fun error(text: String);
    fun setTitle(text: String);
}
public class SwingView: TestRunnerView
{
    val edQuery = JTextArea("Edit")
    val edResult = JTextArea("Result")
    val control = TestRunnerControl(this)
    val closemenu = JMenu("Close");
    val runAction = action("Run", "Run the query under the cursor", null, icon("media_play_green.png")) { run() }


    val guiFrame: JFrame = frame("Funql Runner")
    {
        exitOnClose()

        jmenuBar = menuBar{
            menu("File") {
                add(action("Open") { control.openFile() })
                add(previous())
                add(action("Save Funql") { control.saveFile(false) })
                add(action("Save Funql As") { control.saveFile(true) })
                add(action("Save Results As") { control.saveFile(true, true) })
                add(action("Clear") { control.clear() })
                add(runAction)
            }
            menu("Connections") {
                menu("Open") {
                    add(action("Open Json Text ...") {
                        val props: MutableMap<String, String>? = jsonDriverText(guiFrame)
                        if (props != null)
                            control.createJsonConnection(props)
                    })
                    add(action("Json File ...") {
                        val props = jsonDriverFile(guiFrame)
                        if (props != null)
                            control.createJsonConnection(props)
                    })
                    add(action("MongoDB ...") {
                        val props = mongoDriver(guiFrame)
                        if (props != null)
                            control.createMongoConnection(props)
                    })
                }
                menu("Edit") { }

                add(closemenu)
             }
            menu("Help") {
                add(action("Web Help") {
                    val desktop: Desktop = (if (Desktop.isDesktopSupported()) Desktop.getDesktop() else throw Error("Desktop access not possible to launch web help"))
                    try {
                        desktop.browse(URI("http://www.funql.org/funqlrunner/help"))
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                })
            }


        }
        north = toolbar(){
            add(toolbarbutton(runAction))
        }
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, JScrollPane(edQuery), JScrollPane(edResult))
        splitPane.setDividerLocation(400)
        center = splitPane
    };

    fun toolbarbutton(action: Action): JButton{
        val ret = JButton(action)
        ret.setBorder(null)
        ret.setMargin(Insets(1,1,1,1))
        ret.setHideActionText(true)
        ret.setBackground(null)
        return ret
    }

    {
        closemenu.addMenuListener(object: MenuListener {

            public override fun menuSelected(p0: MenuEvent?) {
                closemenu.removeAll()
                control.connections.forEach{
                    closemenu.add(ConnectionCloser(it))
                }
            }
            public override fun menuDeselected(p0: MenuEvent?) { }
            public override fun menuCanceled(p0: MenuEvent?) { }
        })
        defineShortcutkey(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK), action("Search") {showsearch()})
        defineShortcutkey(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK), action("Search") {run()})
        guiFrame.addWindowListener(object: WindowAdapter() {

            public override fun windowClosing(e: WindowEvent) {
                control.windowClosing()
            }
        })

        control.startUi()
    }

    public fun defineShortcutkey(key : KeyStroke?, action : Action?) : Unit {
        var im : InputMap? = guiFrame.getRootPane()?.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        im?.put(key, action)
        var am : ActionMap? = guiFrame.getRootPane()?.getActionMap()
        am?.put(action, action)
    }

    fun showsearch() {
    }


        fun previous():JMenu{
        val connections = org.funql.ri.gui.prefs.getConnections()
        val ret = JMenu("Reopen ..")
        connections.forEach {
            ret.add(Reopener(it))
        }
        return ret
    }

    fun run() {
        var sel: String? = edQuery.getSelectedText()
        val q = if (sel == null || sel?.length == 0)
        {
            val query: String = edQuery.getText()!!
            val pos = edQuery.getCaretPosition()
            val start = query.lastIndexOf("\n\n", pos)
            val end = query.indexOf("\n\n", pos)
            query.substring(if(start >= 0) start + 2 else 0, if (end >= 0) end else query.length)
        }
        else
            sel
        control.run(q!!)
    }

    public fun jsonDriverText(owner: JFrame): MutableMap<String, String>? {
        val values = formDialog(owner, "Use Json Text for a Connection", 2) {
            a("Connection Name", nonEmpty(JTextField(), control.conNameKey))
            a("Json Text")
            a(1, name(JTextArea(), control.textKey), 1.0, 1.0)
        }
        return values;
    }
    public fun jsonDriverFile(owner: JFrame): MutableMap<String, String>? {
        val values = formDialog(owner, "Use Json Text for a Connection", 3) {
            a("Connection Name", 2, nonEmpty(JTextField(), control.conNameKey), 1.0)
            val edname = nonEmpty(JTextField(), control.fileKey)
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
        val values = formDialog(owner, "Connect to a MongoDB", 2) {
            a("Connection Name", 2, nonEmpty(JTextField(), control.conNameKey), 1.0)
            a("Database Name", nonEmpty(JTextField(), control.dbKey), 1.0)
            a("Host Name", name(JTextField(), control.hostKey), 1.0)
            a("Port Number", name(JTextField(), control.portKey), 1.0)
        }
        return values;
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
        if (ret == JFileChooser.APPROVE_OPTION)
        {
            return fc.getSelectedFile()
        }
        return null;
    }

    protected fun icon(name : String) : Icon {
        try
        {
            val u : URL? = javaClass<SwingView>().getResource("icons/" + name)
            if (u == null)
            {
                System.out.println("Icon " + name + " not found.")
                return javax.swing.plaf.metal.MetalIconFactory.getRadioButtonIcon()!!
            }
            else
                return ImageIcon(u)
        }
        catch (ex : Exception) {
            System.out.println("Icon " + name + " could not be read.")
            return javax.swing.plaf.metal.MetalIconFactory.getRadioButtonIcon()!!

        }

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
    inner class ConnectionCloser(val conn: FunqlConnection): AbstractAction(conn.getName()) {

        public override fun actionPerformed(p0: ActionEvent) {
            control.close(conn)
        }
    }
    inner class Reopener(val conn: MutableMap<String,String>): AbstractAction(conn[Keys.conName.toString()]!!) {

        public override fun actionPerformed(p0: ActionEvent) {
            control.createConnection(conn)
        }
    }
}

