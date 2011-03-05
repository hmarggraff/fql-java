package org.fqlsource.util;

import org.fqlsource.util.NamedImpl;

public class NamedIndex extends NamedImpl
{

    public final int index; // key for fast lookup

    public NamedIndex(String name, int index)
    {
        super(name);
        this.index = index;
    }

    public int getIndex()
    {
        return index;
    }
}
