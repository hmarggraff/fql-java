package org.funql.ri.jsondriver

import org.funql.ri.data.FqlMapContainer
import java.util.ArrayList
import org.funql.ri.util.ListFqlIterator

class JsonListLookup(name: String, val fpath: List<String>, val data: List<Any?>, single: Boolean) : KNamedImpl(name), FqlMapContainer
{
    public override fun lookup(p0: Any?): Any? {
        val ret = ArrayList<Any>()
        for (el in data)
        {
            if (el == null) continue
            var tel: Any? = el
            for (key in fpath){
                val tt = tel
                if (tt is Map<*, *>) tel = tt.get(key)
                else continue
            }
            if (p0.equals(tel)) ret.add(el)
        }
        return ListFqlIterator(ret)
    }
}
