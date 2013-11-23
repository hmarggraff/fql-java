package org.funql.ri.data;

import org.funql.ri.util.Named;

import java.lang.String;

/**
 * Created by hmf on 23.11.13.
 */
public interface FunqlField extends Named {
    FunqlBasicType getType();
    FunqlTypeDef getSubType();
}
