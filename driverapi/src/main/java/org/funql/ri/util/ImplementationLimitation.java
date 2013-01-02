package org.funql.ri.util;

/**
 */
public class ImplementationLimitation extends Error
{
    public ImplementationLimitation()
    {
    }

    public ImplementationLimitation(String message)
    {
	super(message);
    }

    public ImplementationLimitation(String message, Throwable cause)
    {
	super(message, cause);
    }

    public ImplementationLimitation(Throwable cause)
    {
	super(cause);
    }

    public ImplementationLimitation(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
	super(message, cause, enableSuppression, writableStackTrace);
    }
}
