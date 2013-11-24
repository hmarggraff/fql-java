package org.funql.ri.util;

import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.NamedValues;

import java.util.Iterator;

/**
 */
public class FqlIterator4Iterable implements FqlIterator {
    protected final Iterator it;
    protected static String[] names = {"it"};


    public FqlIterator4Iterable(Iterable data) {
	it = data.iterator();
    }

    @Override
    public NamedValues next() {
	if (!it.hasNext())
	    return sentinel;
	Object next = it.next();
	if (next instanceof NamedValues)
	    return (NamedValues) next;
	else
	    return new NamedValuesImpl(names, new Object[] {next});
    }
}
