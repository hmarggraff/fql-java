package org.funql.ri.util;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;

import java.util.List;

/**
 */
public class ListFqlIterator implements FqlIterator {
    protected final List data;
    protected int at;

    public ListFqlIterator(List data) {
        this.data = data;
    }


    @Override
    public Object next() {
        if (!(at < data.size()))
            throw new FqlDataException("List iterator beyond end: " + data.size());
        return data.get(at++);
    }
}
