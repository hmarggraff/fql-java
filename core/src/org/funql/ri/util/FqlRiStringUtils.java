package org.funql.ri.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 */
public class FqlRiStringUtils {
    public static StringBuffer joinList(Collection<String> strings) {
        return joinList(strings, '.');
    }

    public static StringBuffer joinList(Collection<String> strings, char separator) {
        StringBuffer sb = new StringBuffer();
        boolean continuation = false;

        for (String s : strings) {
            if (continuation)
                sb.append(separator);
            else
                continuation = true;

            sb.append(s);
        }
        return sb;
    }

}
