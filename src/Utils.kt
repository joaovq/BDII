import java.util.*
import kotlin.collections.ArrayList

val numberOfEvents = 3
val numberOfTickets = 10
val numberOfThreads = 100
val ticketsIdsPerEvent =  mutableMapOf<Int, ArrayList<Int>>()

fun generateTicketsIds(){
   for(indexEvent in 1..numberOfEvents) {
       val ticketsIds = ArrayList<Int>()
       for (indexTicket in 1..numberOfTickets) {
            val random = Random()
            val ticketId = random.nextInt(10000)
            while(ticketsIds.contains(ticketId));
            while(hasIdAlreadyBeenUsed(ticketId));
           ticketsIds.add(ticketId)
       }
       ticketsIdsPerEvent.put(indexEvent,ticketsIds)
   }
}

private fun hasIdAlreadyBeenUsed(ticketId : Int) : Boolean {
    for ((_, ticketsIds) in ticketsIdsPerEvent) {
        if(ticketsIds.contains(ticketId))
            return true
    }
    return false
}