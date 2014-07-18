package org.funql.ri.util;

import org.funql.ri.data.NamedValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hmf on 18.07.2014.
 */
public class MultiMap<K,V> extends HashMap<K, List<V>> implements Map<K, List<V>>
{
    public List<V> add(K key, V value)
    {
        List<V> existing = super.get(value);
        if (existing == null){
            existing = new ArrayList<V>();
            super.put(key, existing);
        }
        existing.add(value);
        return existing;
    }
}
