package org.funql.ri.data;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 */
public interface FunqlDriver {
    FunqlConnection openConnection(@NotNull String name, Map<String, String> props);
    default boolean supportsRanges() {return false;}
    default boolean isAdvancedDriver() {return false;};
}
