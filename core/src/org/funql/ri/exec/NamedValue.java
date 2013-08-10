package org.funql.ri.exec;

import org.funql.ri.util.NamedImpl;

public abstract class NamedValue extends NamedImpl {
    public NamedValue(String name) {
        super(name);
    }

    public abstract Object getVal();
}
