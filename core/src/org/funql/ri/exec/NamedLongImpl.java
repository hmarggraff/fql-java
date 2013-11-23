package org.funql.ri.exec;

/**
 * Date: 09.03.13 16:15
 */
public class NamedLongImpl extends NamedValueImpl {
    long val;

    public NamedLongImpl(String name, long val) {
        super(name);
        this.val = val;
    }

    @Override public Object getVal() {
        return val;
    }

    public long getLongVal()
    {
        return val;
    }

}
