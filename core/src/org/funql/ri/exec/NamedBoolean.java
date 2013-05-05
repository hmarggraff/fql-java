package org.funql.ri.exec;

/**
 * Date: 09.03.13 16:15
 */
public class NamedBoolean extends NamedValue {
    boolean val;

    public NamedBoolean(String name, boolean val) {
        super(name);
        this.val = val;
    }

    @Override public Object getVal() {
        return val;
    }

    public NamedBoolean(String name) {
        super(name);
    }

    public boolean getBooleanVal()
    {
        return val;
    }

}
