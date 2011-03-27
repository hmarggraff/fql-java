package org.fqlsource.data;


/**
 * A data source, that can iterate of a seqience of Objects.
 * E.g. a database table, that iterates over its rows, or a collection.
 */
public interface FqlStreamContainer<ConnectionType extends DefaultFqlConnection> extends FqlContainer<ConnectionType>, FqlIterator
{
}
