import java.util.*
import kotlin.concurrent.thread

class Usage {
    fun buyTickets() = try {
        for (personNumber in 1..numberOfPeople) {
            thread(start = true) {
                try {
                    val database = Database(getConnection()!!)
                    val random = Random()
                    val events = database.getEvents()
                    val selectedEvent = events[random.nextInt(events.size)]
                    val areas = database.getAreas(selectedEvent.id)
                    val selectedArea = areas[random.nextInt(areas.size)]
                    val ticket = database.getTicket(selectedArea.id)

                    if(ticket != null) {
                        val person = database.getPeople("Person $personNumber")[0]

                        database.updateTicket(person,ticket!!)

                        database.destroy()
                    }

                } catch (e : Exception) {
                    System.err.println(e.javaClass.name + ": " + e.message)
                }
            }
        }
    } catch (e: Exception) {
        System.err.println(e.javaClass.name + ": " + e.message)
        System.exit(0)
    }
}
