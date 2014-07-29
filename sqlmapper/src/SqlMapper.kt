package org.funql.ri.msql
/**
 * Created by hans_m on 24.07.2014.
 */

import org.funql.ri.exec.FqlStatement
import org.funql.ri.exec.clause.FromClause
import org.funql.ri.exec.clause.SelectClause
import org.funql.ri.util.FqlRiStringUtils
import org.funql.ri.kotlinutil.joinList
import org.funql.ri.exec.clause.JoinClause
import java.util.ArrayList
import org.funql.ri.exec.clause.WhereClause
import org.funql.ri.exec.node.FqlNodeInterface
import org.funql.ri.exec.node.BinaryNode
import org.funql.ri.exec.node.MemberNode
import org.funql.ri.exec.node.DotNode
import org.funql.ri.exec.RunEnv
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlDataException
import org.funql.ri.data.FunqlConnection
import org.funql.ri.exec.EntryPointSlot

class SqlMapper(val connectionSlot:EntryPointSlot, val containerName:String, val source: List<FqlStatement>): FqlStatement{
    val mappedStatements = arrayListOf<FqlStatement>(this)
    val sql = toSql()

    public fun toSql(): String {
        val sqlBuilder = StringBuffer()
        var fromTable: String = ""
        val joins = ArrayList<JoinClause>()
        var whereClause: WhereClause? = null
        var selectClause: SelectClause? = null
        for (clause in source){
            when (clause) {
                is FromClause -> fromTable = clause.getConnectionSlot()!!.getEntryPointName()!!
                is SelectClause -> selectClause = clause
                is JoinClause -> joins.add(clause)
                is WhereClause ->  whereClause = clause
                else -> mappedStatements.add(clause)
            }
        }
        sqlBuilder.append("select ")
        if (selectClause != null)
            joinList(selectClause!!.getFieldNames()!!, ',', sqlBuilder)
        else
            sqlBuilder.append('*')
        sqlBuilder.append("\n")

        sqlBuilder.append("from ", fromTable,"\n")
        for (j in joins){
            sqlBuilder.append("join ",  j.containerName, " on ")

            mapExpression(j.joinExpression, sqlBuilder)
            sqlBuilder.append("\n")
        }
        if (whereClause != null){
            sqlBuilder.append("where ")
            whereClause!!.getExpr()!!.dump(sqlBuilder)
            sqlBuilder.append("\n")
        }
        return sqlBuilder.toString()
    }

    fun mapExpression(node: FqlNodeInterface?, sqlBuilder: StringBuffer): Boolean {
        when (node) {
            is BinaryNode -> binary(node, sqlBuilder)
            is MemberNode -> sqlBuilder.append(node.getMemberName())
            is DotNode -> {
                mapExpression(node.getOperand(), sqlBuilder)
                sqlBuilder.append(".", node.getMemberName())
            }
            else -> sqlBuilder.append("[", node.javaClass.getName(), "]")
        }
        return false
    }

    fun binary(node: BinaryNode, sqlBuilder: StringBuffer){
        mapExpression(node.getLeft(), sqlBuilder)
        sqlBuilder.append(node.getOperator(), sqlBuilder)
        mapExpression(node.getOperand(), sqlBuilder)
    }



    override fun execute(env: RunEnv?, precedent: FqlIterator?): FqlIterator  {
        val funqlConnection = env!!.getConnection(connectionSlot.getIndex())!! as MappedSqlConnection
        val listContainer = funqlConnection.getIterator(sql)!!
        return listContainer
    }
}