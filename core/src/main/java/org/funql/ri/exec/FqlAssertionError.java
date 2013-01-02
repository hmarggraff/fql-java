package org.funql.ri.exec;

public class FqlAssertionError extends Error
{
    public FqlAssertionError(String s, int row, int col)
    {
        super(s + " Row:" + row + " Col:" + col);
    }


}
