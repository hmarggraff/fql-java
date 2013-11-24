package org.funql.ri.util;


import org.funql.ri.data.NamedValues;

import java.util.Map;

/**
 * Created by hmf on 24.11.13.
 */
public class NamedValuesImpl implements NamedValues {
    protected final String[] names;
    protected final Object[] values;

    public NamedValuesImpl(String[] names, Object[] values) {
	this.names = names;
	this.values = values;
    }

    public NamedValuesImpl(Map<String, Object> src) {
	names = new String[src.size()];
	values = new Object[src.size()];
	int cnt = 0;
	for (Map.Entry<String, Object> e : src.entrySet()) {
	    names[cnt] = e.getKey();
	    values[cnt] = e.getValue();
	}
    }

    public NamedValuesImpl(String name, Object value) {
	this.names = new String[]{name};
	this.values = new Object[]{value};
    }


    public String[] getNames() {
	return names;
    }

    public Object[] getValues() {
	return values;
    }
}
