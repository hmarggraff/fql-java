package org.funql.ri.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hmf on 18.07.2014.
 */
public class MultiMapBoolean<K,V> extends HashMap<K, MultiMapBoolean.ListBoolPair<V>> implements Map<K, MultiMapBoolean.ListBoolPair<V>>
{
    public List<V> getList(K key){
        final ListBoolPair<V> listBoolPair = super.get(key);
        if (listBoolPair != null)
            return listBoolPair.getValues();
        return null;
    }
    public List<V> add(K key, V value)
    {
        ListBoolPair<V> existing = super.get(value);
        if (existing == null){
            existing = new ListBoolPair();
            super.put(key, existing);
        }
        existing.values.add(value);
        return existing.values;
    }

    public static class ListBoolPair<V> {
        boolean seen;
        List<V> values = new ArrayList<V>();

        public List<V> getValues()
        {
            return values;
        }

        public boolean isSeen()
        {
            return seen;
        }

        public void setSeen(boolean seen)
        {
            this.seen |= seen;
        }
    }
}
