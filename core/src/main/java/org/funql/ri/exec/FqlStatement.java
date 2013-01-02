package org.funql.ri.exec;

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;

public interface FqlStatement
{
    FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException;
}
