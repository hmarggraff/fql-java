package org.funql.ri.simpletestdriver;

import org.funql.ri.data.NamedValues;
import org.funql.ri.exec.Updater;
import org.funql.ri.util.NamedValuesImpl;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by hmf on 07.08.2014.
 */
public class SimpleTestUpdater implements Updater
{
    private final String[] fieldNames;
    public final LinkedHashMap<Object, Object> data = new LinkedHashMap();

    public SimpleTestUpdater(@NotNull String[] fieldNames)
    {
        this.fieldNames = fieldNames;
    }


    @Override
    public NamedValues put(Object[] values)
    {
        data.put(data.size(), buildMap(values));
        return new NamedValuesImpl("id", data.size() - 1);
    }

    @Override
    public void put(Object[] value, Object key)
    {
        data.put(key, buildMap(value));

    }

    @Override
    public void commit()
    {

    }

    protected HashMap<String, Object> buildMap(Object[] value)  {
        final HashMap<String, Object> obj = new HashMap<>();
        for (int i = 0; i < fieldNames.length; i++)
            obj.put(fieldNames[i], value[i]);
        return obj;

    }

}
