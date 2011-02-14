package org.fqlsource.data;// created Jul 11, 2010

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

    public FqlDataException(Throwable cause)
    {
        super(cause);
    }
}
