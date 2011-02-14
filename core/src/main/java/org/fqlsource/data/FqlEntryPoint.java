package org.fqlsource.data;

/**
 */
public interface FqlEntryPoint<ConnectionType extends FqlConnection> extends Iterable
{
    ConnectionType getConnection();
}
