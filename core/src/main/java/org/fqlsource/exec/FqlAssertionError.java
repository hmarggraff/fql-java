package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

public class FqlAssertionError extends Error
{
    public FqlAssertionError(String s, int row, int col)
    {
        super(s + " Row:" + row + " Col:" + col);
    }


}
