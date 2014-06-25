package org.funql.ri.exec;


import org.funql.ri.data.NamedValues;

/**
 * Created by hmf on 03.11.13.
 */
public interface Updater {
    /**
     * puts the values into the database as one record with the names supplied.
     * @param values the values for each field
     * @return the primary key (OID) for this record
     */
    public NamedValues put(Object[] values);
    public void put(Object[] value, Object key);
    public void commit();
    }
