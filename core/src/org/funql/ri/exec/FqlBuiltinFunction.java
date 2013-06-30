package org.funql.ri.exec;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.util.Named;
import org.funql.ri.util.NotYetImplementedError;
import org.funql.ri.util.NamedImpl;

/**
 */
public interface FqlBuiltinFunction
{
    public String name();
    public abstract Object val(RunEnv env, Object from, Object[] argvals) throws FqlDataException;
}
