package org.funql.ri.sqldriver.mapping

/**
 * Created by hmf on 24.07.2014.
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
import org.funql.ri.exec.ContainerSlot
import org.funql.ri.sqldriver.mapping.MappedSqlConnection

class SqlMapper(val connectionSlot: ContainerSlot, val containerName:String, val source: List<FqlStatement>): org.funql.ri.exec.FqlStatement {
    val mappedStatements = arrayListOf<FqlStatement>()
    val sql = toSql()

    public fun toSql(): String {
        val sqlBuilder = StringBuffer()
        var fromTable: String = ""
        val joins = java.util.ArrayList<JoinClause>()
        var whereClause: WhereClause? = null
        var selectClause: SelectClause? = null
        for (clause in source){
            when (clause) {
                is FromClause -> fromTable = clause.getConnectionSlot()!!.getContainerName()
                is SelectClause -> selectClause = clause
                is JoinClause -> joins.add(clause)
                is WhereClause ->  whereClause = clause
                else -> mappedStatements.add(clause)
            }
        }
        sqlBuilder.append("select ")
        if (selectClause != null)
            selectClause!!.getFieldNames()?.joinTo(sqlBuilder, ",")
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

    fun mapExpression(node: org.funql.ri.exec.node.FqlNodeInterface?, sqlBuilder: StringBuffer): Boolean {
        when (node) {
            is org.funql.ri.exec.node.BinaryNode -> binary(node, sqlBuilder)
            is org.funql.ri.exec.node.MemberNode -> sqlBuilder.append(node.getMemberName())
            is org.funql.ri.exec.node.DotNode -> {
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



    override fun execute(env: org.funql.ri.exec.RunEnv?, precedent: org.funql.ri.data.FqlIterator?): org.funql.ri.data.FqlIterator {
        val funqlConnection = env!!.getConnection(connectionSlot.getIndex())!! as MappedSqlConnection
        val listContainer = funqlConnection.getIterator(sql)
        return listContainer
    }
}

