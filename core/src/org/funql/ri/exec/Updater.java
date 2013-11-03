package org.funql.ri.exec;

/**
 * Created by hmf on 03.11.13.
 */
public interface Updater {
    /**
     * puts the values into the database as one record with the names supplied.
     * @param fieldNames the list of the names to be stored into
     * @param value the values for each field
     * @return the primary key (OID) for this record
     */
    public Object put(String[] fieldNames, Object[] value);
}
