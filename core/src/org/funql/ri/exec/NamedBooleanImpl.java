package org.funql.ri.exec;

/**
 * Date: 09.03.13 16:15
 */
public class NamedBooleanImpl extends NamedValueImpl {
    boolean val;

    public NamedBooleanImpl(String name, boolean val) {
        super(name);
        this.val = val;
    }

    @Override public Object getVal() {
        return val;
    }

    public NamedBooleanImpl(String name) {
        super(name);
    }

    public boolean getBooleanVal()
    {
        return val;
    }

}
