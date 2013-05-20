package org.funql.ri.mongokotlinwrapper

import com.mongodb.Mongo
import com.mongodb.DB


public fun Mongo.get(dbName:String):DB = this.getDB(dbName)!!

