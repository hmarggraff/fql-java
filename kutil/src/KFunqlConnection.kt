package org.funql.ri.kotlinutil
/**
 * Created by hmf on 10.11.13.
 */


import org.funql.ri.data.FunqlConnectionWithRange
import org.funql.ri.data.FqlIterator
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.exec.Updater
import java.util.UUID

public abstract class KFunqlConnection(name: String): FunqlConnectionWithRange, KNamedImpl(name){

    override fun range(name: String?, startKey: String?, endKey: String?, includeEnd: Boolean): FqlIterator? = krange(name!!,startKey!!,endKey!!,includeEnd)
    override fun useMap(name: String?, fieldpath: List<String>?, single: Boolean): FqlMapContainer? = kuseMap(name!!, fieldpath!!, single)
    override fun getMember(from: Any?, member: String?): Any? = kgetMember(from, member!!)
    override fun getUpdater(targetName: String?, fieldNames: Array<out String>?): Updater? = kgetUpdater(targetName!!, fieldNames!!)
    override fun getIterator(streamName: String?): FqlIterator? = kgetIterator(streamName!!)
    override fun nextSequenceValue(sequenceName: String?): Any? = knextSequenceValue(sequenceName!!)
    abstract fun krange(name: String, startKey: String, endKey: String, includeEnd: Boolean): FqlIterator


    abstract fun kuseMap(name: String, fieldpath: List<String>, single: Boolean): FqlMapContainer?
    abstract fun kgetMember(from: Any?, member: String): Any?
    abstract fun kgetUpdater(targetName: String, fieldNames: Array<out String>): Updater?
    abstract fun kgetIterator(streamName: String): FqlIterator
    open fun knextSequenceValue(sequenceName: String): Any = UUID.randomUUID()

}