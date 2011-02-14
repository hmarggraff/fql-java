package org.fqlsource.exec;

import org.fqlsource.data.FqlDataException;

import java.util.Iterator;

public interface FqlStatement
{
    Iterable execute(RunEnv env, Iterator precedent) throws FqlDataException;
}
