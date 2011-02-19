package org.fqlsource.data;// created Jul 11, 2010

import org.fqlsource.exec.FqlNode;
import org.fqlsource.exec.FqlNodeInterface;

public class FqlDataException extends Exception
{
    public FqlDataException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

    public FqlDataException(String message)
    {
        super(message);
    }

    public FqlDataException(Throwable cause, int row, int col)
    {
        super(cause);
    }

    public FqlDataException(String s, int row, int col)
    {
        super(s + " Row:" + row + " Col:" + col);
    }

    public FqlDataException(String s, FqlNodeInterface node)
    {
        super(s + " Row:" + node.getRow() + " Col:" + node.getCol());
    }
}
