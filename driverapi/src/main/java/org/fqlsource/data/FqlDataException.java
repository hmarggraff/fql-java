package org.fqlsource.data;


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

    /*
    public FqlDataException(String s, FqlNodeInterface node)
    {
        super(s + " Row:" + node.getRow() + " Col:" + node.getCol());
    }
    */
}
