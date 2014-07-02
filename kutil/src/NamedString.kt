package org.funql.ri.kotlinutil

import org.funql.ri.util.Named

public open class NamedString(name1: String, public val value:String): KNamedImpl(name1){
    public override fun toString(): String = name1
}
public open class NamedStringPair(name1: String, value:String, public val value2:String): NamedString(name1, value)
