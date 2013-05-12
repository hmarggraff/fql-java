package org.fqlsource.fqltest.simpletestdriver;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;

/**
 */
public class SimpleTestRange implements FqlIterator
{
    private final int start;
    private final int end;
    private int at;
    private final boolean includeEnd;

    public SimpleTestRange(int start, int end, boolean includeEnd)
    {
	this.start = start;
	this.end = end;
	this.includeEnd = includeEnd;
    }

    @Override
    public boolean hasNext()
    {
	if (includeEnd)
		return at <= end;
	else
	    return at < end;
    }

    @Override
    public Object next()
    {
	if (!hasNext())
	    throw new FqlDataException("Mock iterator beyond end: " + end);
	at++;
	return at;
    }

    @Override
    public Object current()
    {
	if ((includeEnd && at > end) || at >= end)
	    throw new FqlDataException("Mock iterator beyond end: " + end);
	return at;
    }
}
