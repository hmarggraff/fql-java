package org.fqlsource.exec;

import org.fqlsource.exec.FqlAssertionError;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Holds the 2 parts of a concatenated collection, so that it is possible to iterate over them in sequence.
 */
public class CollectionSlice implements Iterable
{
    protected final Object srcValue;
    private final int start;
    private final int end;
    protected final int row;
    protected final int col;

    public CollectionSlice(Object srcValue, int start, int end, int row, int col)
    {
        this.srcValue = srcValue;
        this.start = start;
        this.end = end;
        this.row = row;
        this.col = col;
    }

    public Iterator iterator()
    {
        if (end - start < 0)
        {
            return new EmptyIterator();
        }
        else if (srcValue instanceof List)
        {
            final List list = (List) srcValue;
            if (start >= list.size())
            {
                return new EmptyIterator();
            }
            int maxEnd = Math.min(end, list.size());
            return list.subList(start, maxEnd).iterator();
        }
        else if (srcValue.getClass().isArray())
        {
            final Object[] objects = (Object[]) srcValue;
            if (start >= objects.length)
            {
                return new EmptyIterator();
            }
            int maxEnd = Math.min(end, objects.length);
            Object[] slice = new Object[maxEnd - start];
            System.arraycopy(srcValue, start, slice, 0, maxEnd - start);
            return Arrays.asList(slice).iterator();
        }
        else if (srcValue instanceof Iterable)
        {
            return new CollectionSliceIterator(((Iterable) srcValue).iterator());
        }
        else
        {
            throw new FqlAssertionError("Source of collection slice is not an Iterable or Array: " + srcValue.getClass(), row, col);
        }
    }

    class CollectionSliceIterator implements Iterator
    {

        int at;
        Iterator it;

        CollectionSliceIterator(Iterator it)
        {
            this.it = it;
            while (at < start && it.hasNext())
            {
                it.next();
                at++;
            }
        }

        public boolean hasNext()
        {

            return at < end && it.hasNext();
        }

        public Object next()
        {
            at++;
            return it.next();
        }

        public void remove()
        {
            throw new FqlAssertionError("Remove from collections will not be supported", row, col);
        }
    }

    static class EmptyIterator implements Iterator
    {
        public boolean hasNext()
        {
            return false;
        }

        public Object next()
        {
            return null;
        }

        public void remove()
        {
        }
    }

}
