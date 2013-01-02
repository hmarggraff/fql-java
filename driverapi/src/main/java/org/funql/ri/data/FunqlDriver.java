package org.funql.ri.data;

import java.util.Map;

/**
 */
public interface FunqlDriver
{
    FqlConnection openConnection(String name, Map<String, String> props);
    boolean supportsRanges();
    boolean isAdvancedDriver();
}
