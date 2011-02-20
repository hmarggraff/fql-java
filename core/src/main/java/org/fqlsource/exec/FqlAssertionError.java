package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.parser.FqlParser;

public class FqlAssertionError extends Error
{
    public FqlAssertionError(String s, int row, int col)
    {
        super(s + " Row:" + row + " Col:" + col);
    }


}
