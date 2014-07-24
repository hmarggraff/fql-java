/**
 * Created by hans_m on 24.07.2014.
 */

import org.funql.ri.exec.FqlStatement
import org.funql.ri.exec.clause.FromClause
import org.funql.ri.exec.clause.SelectStatement
import org.funql.ri.util.FqlRiStringUtils
import org.funql.ri.kotlinutil.joinList
import org.funql.ri.exec.clause.JoinClause
import java.util.ArrayList
import org.funql.ri.exec.clause.WhereClause
import org.funql.ri.exec.node.FqlNodeInterface
import org.funql.ri.exec.node.BinaryNode
import org.funql.ri.exec.node.MemberNode
import org.funql.ri.exec.node.DotNode

class SqlMapper(val source: List<FqlStatement>){
    val sql = StringBuffer()
    public fun toSql(): String {
        var fromTable: String = ""
        val joins = ArrayList<JoinClause>()
        var whereClause: WhereClause? = null
        var selectStatement: SelectStatement? = null
        for (clause in source){
            when (clause) {
                is FromClause -> fromTable = clause.getConnectionSlot()!!.getEntryPointName()!!
                is SelectStatement -> selectStatement = clause
                is JoinClause -> joins.add(clause)
                is WhereClause ->  whereClause = clause
            }
        }
        sql.append("select ")
        if (selectStatement != null)
            joinList(selectStatement!!.getFieldNames()!!, ',', sql)
        else
            sql.append('*')
        sql.append("\n")

        sql.append("from ", fromTable,"\n")
        for (j in joins){
            sql.append("join ",  j.containerName, " on ")

            process(j.joinExpression)
            sql.append("\n")
        }
        if (whereClause != null){
            sql.append("where ")
            whereClause!!.getExpr()!!.dump(sql)
            sql.append("\n")
        }
        return sql.toString()
    }

    fun process(node: FqlNodeInterface?): Boolean {
        when (node) {
            is BinaryNode -> binary(node)
            is MemberNode -> sql.append(node.getMemberName())
            is DotNode -> {
                process(node.getOperand())
                sql.append(".", node.getMemberName())
            }
            else -> sql.append("[", node.javaClass.getName(), "]")
        }
        return false
    }

    fun binary(node: BinaryNode){
        process(node.getLeft())
        sql.append(node.getOperator())
        process(node.getOperand())
    }

}