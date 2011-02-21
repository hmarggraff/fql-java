package org.fqlsource.data;

/**
 */
public interface FqlEntryPoint<ConnectionType extends DefaultFqlConnection> extends Iterable
{
    ConnectionType getConnection();
}
