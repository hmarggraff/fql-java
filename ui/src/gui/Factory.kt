package org.funql.ri.gui

/**
 * Date: 11.05.13 13:10
 */

enum class RunMode {
    run
    test
}



object Factory {
    var mode = RunMode.run // default

    fun any(run:  Any, test:Any): Any {
       when(mode){
           RunMode.run -> return run
           RunMode.test -> return test
       }
        return run
    }
    fun str(run:  String, test:String):String {
       when(mode){
           RunMode.run -> return run
           RunMode.test -> return test
       }
        return run  // nor required, but compiler doesn't know
    }
    //{ println ("RunMode = $mode")}
    val prefKey = str("funqlrunner", "funqlrunnertest")

    fun testMode() {mode = RunMode.test}
}

