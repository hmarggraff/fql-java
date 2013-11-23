package org.funql.ri.data;

import java.util.Date;

/**
 * Created by hmf on 23.11.13.
 */
public interface FunqlIteratorElement {
    FunqlTypeDef getType();
    Object get(int fieldIndex);
    String getString(int fieldIndex);
    Integer getInt(int fieldIndex);
    Double getDouble(int fieldIndex);
    Boolean getBoolean(int fieldIndex);
    Date getDate(int fieldIndex);
    byte[] getBinary(int fieldIndex);
    FunqlIteratorElement getRef(int fieldIndex);
    FunqlIteratorElement getRef(int fieldIndex, FqlMapContainer container);
    FqlIterator getIterator(int fieldindex);
    FunqlIteratorElement getInner(int fieldindex);
}
