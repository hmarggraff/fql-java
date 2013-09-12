package org.fqlsource.fqltest.simpletestdriver;

import org.funql.ri.data.FqlMapContainer;
import org.funql.ri.util.FqlRiStringUtils;
import org.funql.ri.util.NamedImpl;

import java.util.List;

/**
 */
public class SimpleTestMap extends NamedImpl implements FqlMapContainer {
    protected final String fieldStr;
    private boolean single;
    SimpleTestConnection conn;


    public SimpleTestMap(SimpleTestConnection conn, String name, List<String> path, boolean single) {
        super(name);
        this.conn = conn;
        this.single = single;
        fieldStr = FqlRiStringUtils.joinList(path, '_');
    }

    @Override
    public Object lookup(Object key) {
        if (single) {
            if (fieldStr.startsWith("L"))
                return SimpleTestConnection.letterNum(fieldStr);
            else if (fieldStr.startsWith("D"))
                return SimpleTestConnection.getDouble(fieldStr);
            else if (fieldStr.startsWith("T"))
                return SimpleTestConnection.getTime(fieldStr);
            else if ("yes".equalsIgnoreCase(fieldStr))
                return true;
            else if ("no".equalsIgnoreCase(fieldStr))
                return false;
            return fieldStr + "_" + key.toString();
        }
        else
            return new SimpleTestIterator(conn, fieldStr, SimpleTestConnection.letterNum(fieldStr));
    }
}
