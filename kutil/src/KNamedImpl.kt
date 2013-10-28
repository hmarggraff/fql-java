package org.funql.ri.kotlinutil

import org.funql.ri.util.Named

public open class KNamedImpl(private val name1: String): Named {
    public override fun getName(): String = name1

    public override fun compareTo(other: Named): Int = name1.compareTo(other.getName()!!)
}
