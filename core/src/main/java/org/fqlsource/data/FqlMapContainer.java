package org.fqlsource.data;

import org.fqlsource.exec.RunEnv;

/**
 */
public interface FqlMapContainer<ConnectionType extends FqlConnection> extends FqlContainer<ConnectionType>
{

    Object lookup(RunEnv runEnv, Object key);
}
