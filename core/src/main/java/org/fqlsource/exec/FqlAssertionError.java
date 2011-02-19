package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

public class FqlAssertionError extends FqlDataException
{
    public FqlAssertionError(String s, FqlNodeInterface node)
    {
        super(s, node);
    }
}
