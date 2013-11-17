package org.fqlsource.fqltest.simpletestdriver;

import org.funql.ri.data.FunqlConnection;
import org.funql.ri.data.FunqlDriver;

import java.util.Map;

/**
 */
public class SimpleTestDriver implements FunqlDriver {
    @Override
    public FunqlConnection openConnection(String name, Map<String, String> props) {
	return new SimpleTestConnection(name, props);
    }

    @Override
    public boolean supportsRanges() {
	return true;
    }

    @Override
    public boolean isAdvancedDriver() {
	return false;
    }

    protected static long sequenceCounter = 0l;

}
