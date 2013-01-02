package org.funql.ri.exec;

import org.funql.ri.util.NotYetImplementedError;
import org.funql.ri.util.NamedImpl;

/**
 */
public class FqlBuiltinFunction extends NamedImpl
{
    public FqlBuiltinFunction(String name)
    {
        super(name);
    }

    public Object val(RunEnv env, Object from, Object[] argvals)
    {
        throw new NotYetImplementedError();
    }

}
