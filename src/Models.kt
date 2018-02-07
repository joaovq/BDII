import java.sql.Date

data class Event(val id: Int, val name: String, val location: String, val date: Date)
data class Area(val id: Int, val name: String, val capacity: Int, val event: Int)
data class Ticket(val id: Int, val available: Boolean, val area: Int, val price: Int)
data class Person(val name: String, val age: Int, val address: String)
data class Buy(val id : Int, val person : Int, val ticket : Int, val isPaid : Boolean, val due_date : Date)

