package org.funql.ri.exec;

/**
 * Date: 09.03.13 16:15
 */
public class NamedDoubleImpl extends NamedValueImpl {
    double val;

    public NamedDoubleImpl(String name, double val) {
        super(name);
        this.val = val;
    }

    @Override public Object getVal() {
        return val;
    }

    public NamedDoubleImpl(String name) {
        super(name);
    }

    public double getDoubleVal()
    {
        return val;
    }

}
