package kotlin.swing

import java.awt.event.ActionEvent
import java.awt.event.ActionListener

public class FunctionActionListener(val fn: (ActionEvent) -> Unit) : ActionListener {
    public override fun actionPerformed(p0: ActionEvent) {
        fn(p0)
    }
}
