package org.fqlsource.ri.util;

import org.funql.ri.data.FqlMapContainer;

/**
 * A map container that simply returns null. Can be used if there is no lookup functionality or no data.
 */
public class FqlNullMap implements FqlMapContainer
{
    @Override
    public Object lookup(Object key)
    {
	return null;
    }
}
