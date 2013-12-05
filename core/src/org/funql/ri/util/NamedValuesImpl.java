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

    @Override
    public String toString() {
	if (names.length== 1 && "it".equals(names[0]))
	    return FqlRiStringUtils.toString(values[0]);
	StringBuilder sb = new StringBuilder('{');
	for (int i = 0; i < names.length-1; i++){
	    if (i > 0) sb.append(',');
	    sb.append(names[i]).append(':').append(values[i]);
	}
	sb.append("}");

	return super.toString();
    }
}
