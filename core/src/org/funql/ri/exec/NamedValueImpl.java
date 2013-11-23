package org.funql.ri.exec;

import org.funql.ri.util.NamedImpl;

public abstract class NamedValueImpl extends NamedImpl {
    public NamedValueImpl(String name) {
        super(name);
    }

    public abstract Object getVal();
}
