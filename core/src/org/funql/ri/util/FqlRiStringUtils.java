package org.funql.ri.util;

import java.util.Collection;

/**
 */
public class FqlRiStringUtils {
    public static String joinList(Collection<String> strings) {
        return joinList(strings, '.');
    }

    public static String joinList(Collection<String> strings, char separator) {
        StringBuffer sb = new StringBuffer();
        if (strings != null) {
            boolean continuation = false;

            for (String s : strings) {
                if (continuation)
                    sb.append(separator);
                else
                    continuation = true;
                sb.append(s);
            }
        }
        return sb.toString();
    }

    public static String toString(Object value) {
	if (value == null)
	    return "null";
	else
	    return value.toString();
    }
}
