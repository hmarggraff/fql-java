package org.funql.ri.sqldriver

import java.sql.ResultSet
import org.funql.ri.kotlinutil.KNamedImpl
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.NamedValues
import org.funql.ri.util.NamedValuesImpl
import kotlin.jdbc.getColumnNames

public open class SqlResultSetIterator(name: String, val data: ResultSet) : KNamedImpl(name), FqlIterator
{
    val names: Array<String> = data.getColumnNames()

    override fun next(): NamedValues? {
        if (!data.next())
            return FqlIterator.sentinel
        val values = Array(names.size) { data.getObject(it+1) }

        [suppress("CAST_NEVER_SUCCEEDS")]
        return NamedValuesImpl(names, values as Array<Any>);
    }
}