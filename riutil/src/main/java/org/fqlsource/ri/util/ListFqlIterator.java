package org.fqlsource.ri.util;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;

import java.util.List;

/**
 */
public class ListFqlIterator implements FqlIterator
{
    protected List data;
    protected int at;

    public ListFqlIterator(List data)
    {
	this.data = data;
    }

    @Override
    public boolean hasNext()
    {
	return at < data.size();
    }

    @Override
    public Object next()
    {
	if (!hasNext())
	    throw new FqlDataException("List iterator beyond end: " + data.size());
	return data.get(at++);
    }

    @Override
    public Object current()
    {
	if (at >= data.size())
	    throw new FqlDataException("List iterator beyond end: " + data.size());
	return data.get(at);
    }
}
