package org.funql.ri.data;

import org.jetbrains.annotations.NotNull;

/**
 * Created by hmf on 23.11.13.
 */
public interface NamedValues {
    @NotNull String[] getNames();
    @NotNull Object[] getValues();
}
