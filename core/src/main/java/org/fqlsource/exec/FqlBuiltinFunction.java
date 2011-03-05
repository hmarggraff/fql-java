package org.fqlsource.exec;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.util.NamedImpl;

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
