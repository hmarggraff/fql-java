package org.funql.ri.kotlinutil
/**
 * Created by hans_m on 08.07.2014.
 */
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.ConfigurationFactory

fun logger(name:String):Logger {
    val configurationFactory: ConfigurationFactory? = ConfigurationFactory.getInstance()
    return LogManager.getLogger(name)!!
}