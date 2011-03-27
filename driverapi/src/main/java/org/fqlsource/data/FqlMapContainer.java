package org.fqlsource.data;


/**
 */
public interface FqlMapContainer<ConnectionType extends FqlConnection> extends FqlContainer<ConnectionType>
{

    Object lookup(RunEnv runEnv, Object key);
}
