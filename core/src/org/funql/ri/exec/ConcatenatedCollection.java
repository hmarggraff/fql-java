package org.funql.ri.exec;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Holds the 2 parts of a concatenated collection, so that it is possible to iterate over them in sequence.
 */
public class ConcatenatedCollection implements Iterable
{
    protected final Object leftValue;
    protected final Object rightValue;
    protected final int row;
    protected final int col;

    public ConcatenatedCollection(Object leftValue, Object rightValue, int row, int col)
    {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        this.row = row;
        this.col = col;
    }

    public Iterator iterator()
    {
        return new ConcatenatedCollectionIterator();
    }

    class ConcatenatedCollectionIterator implements Iterator
    {
        boolean second;
        Iterator it;

        ConcatenatedCollectionIterator()
        {


            if (leftValue.getClass().isArray())
                it = Arrays.asList((Object[]) leftValue).iterator();
            else if (leftValue instanceof Iterable)
                it = ((Iterable) leftValue).iterator();
            else
                throw new FqlAssertionError("Left value of concatenation is not an Iterable or Array: " + leftValue.getClass(), row, col);

        }

        public boolean hasNext()
        {
            if (second)
                return it.hasNext();
            // we are processing the left value;
            if (it.hasNext())
                return true;
            // first value exhausted, continue with right value
            if (rightValue.getClass().isArray())
                it = Arrays.asList((Object[]) rightValue).iterator();
            else if (rightValue instanceof Iterable)
                it = ((Iterable) rightValue).iterator();
            else
                throw new FqlAssertionError("Right value of concatenation is not an Iterable or Array: " + rightValue.getClass(), row, col);
            return it.hasNext();
        }

        public Object next()
        {
            return it.next();
        }

        public void remove()
        {
            throw new FqlAssertionError("Right value of concatenation is not an Iterable or Array: " + rightValue.getClass(), row, col);
        }
    }
}
