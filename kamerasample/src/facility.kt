package org.funql.ri.test.facility

import java.util.ArrayList
import java.util.Date

class Building(id:String, val levels: Array<Level>)
class Level(rooms: Array<Room>)
open class Room(id:String, sqm: Int)  // Base class and unused room
open class MeetingRoom(id:String, sqm: Int, phone: String):Room(id, sqm){
    val equipment = ArrayList<Equipment>()
    val occupation = ArrayList<Meeting>()
}
open class OfficeSpace(id:String, sqm: Int):Room(id, sqm){
    val desks = ArrayList<Desk>()
}
open class Kitchen(id:String, sqm: Int):Room(id, sqm)
open class Garage(id:String, sqm: Int, carCapacity: Int):Room(id, sqm)

class Desk(var occupiedBy:String, var phone: String)
class Equipment(val typ:String, var count:Int)

class Meeting(val start: Date, val end: Date, val bookedBy: String)