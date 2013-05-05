package org.fqlsource.fqltest.simpletestdriver;

import org.funql.ri.data.FqlMapContainer;
import org.fqlsource.ri.util.FqlRiStringUtils;
import org.funql.ri.util.NamedImpl;

import java.util.List;

/**
 */
public class SimpleTestMap extends NamedImpl implements FqlMapContainer
{
    protected final String fieldStr;


    public SimpleTestMap(List<String> path)
    {
	super(FqlRiStringUtils.joinList(path, '_').toString());
	fieldStr = path.get(path.size()-1);
    }

    @Override
    public Object lookup(Object key)
    {
	String fieldName = key.toString();
	if (fieldName.startsWith("L"))
	    return SimpleTestConnection.letterNum(fieldName);
	else if (fieldName.startsWith("D"))
	    return SimpleTestConnection.getDouble(fieldName);
	else if (fieldName.startsWith("T"))
	    return SimpleTestConnection.getTime(fieldName);
	else if ("yes".equalsIgnoreCase(fieldName))
	    return true;
	else if ("no".equalsIgnoreCase(fieldName))
	    return false;
	return "from " + getName() + " where " + fieldStr + "=" + key.toString();
    }
}
