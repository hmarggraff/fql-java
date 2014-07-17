package gui

import org.funql.ri.util.Keys

/**
 * Created by hmf on 10.07.2014.
 */


class MapComboDisplay(val content: Map<String, String>){
    override fun toString(): String = content[Keys.connection]!!
}
