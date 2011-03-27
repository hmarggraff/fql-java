package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlIterator;
import org.fqlsource.data.RunEnv;

public interface FqlStatement
{
    FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException;
}
