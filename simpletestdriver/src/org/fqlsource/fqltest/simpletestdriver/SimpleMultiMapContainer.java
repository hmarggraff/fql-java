package org.fqlsource.fqltest.simpletestdriver;

import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.FqlMultiMapContainer;
import org.funql.ri.util.FqlRiStringUtils;

import java.util.List;

/**
 */
public class SimpleMultiMapContainer implements FqlMultiMapContainer
{
    protected final String fieldStr;
    private final SimpleTestConnection connection;


    public SimpleMultiMapContainer(List<String> path, final SimpleTestConnection connection)
    {
	this.connection = connection;
	fieldStr = FqlRiStringUtils.joinList(path, '_').toString();
    }

    @Override
    public FqlIterator lookup(Object key)
    {
	return connection.getIterator(fieldStr);
    }
}
