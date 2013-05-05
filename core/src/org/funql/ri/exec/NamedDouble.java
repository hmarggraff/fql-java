package org.funql.ri.exec;

/**
 * Date: 09.03.13 16:15
 */
public class NamedDouble extends NamedValue {
    double val;

    public NamedDouble(String name, double val) {
        super(name);
        this.val = val;
    }

    @Override public Object getVal() {
        return val;
    }

    public NamedDouble(String name) {
        super(name);
    }

    public double getDoubleVal()
    {
        return val;
    }

}
