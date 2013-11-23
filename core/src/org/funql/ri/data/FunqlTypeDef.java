package org.funql.ri.data;


import org.funql.ri.util.Named;

import java.util.List;

/**
 * Created by hmf on 23.11.13.
 */
public interface FunqlTypeDef extends Named {
    List<FunqlField> getMembers();
}
