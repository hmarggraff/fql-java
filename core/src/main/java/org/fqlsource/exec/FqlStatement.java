package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

import java.util.Iterator;

public interface FqlStatement
{
    FqlIterator execute(RunEnv env, FqlIterator precedent) throws FqlDataException;
}
