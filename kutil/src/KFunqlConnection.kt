package org.funql.ri.kotlinutil
/**
 * Created by hmf on 10.11.13.
 */


import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.exec.Updater
import java.util.UUID
import org.funql.ri.data.FunqlConnection

public abstract class KFunqlConnection(name: String): FunqlConnection, KNamedImpl(name){
    override fun toString() = getName()
    override fun nextSequenceValue(sequenceName: String): Any = UUID.randomUUID()
}