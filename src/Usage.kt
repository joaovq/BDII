import java.util.*
import kotlin.concurrent.thread

class Usage {


    fun buyTickets() {
        try {
            for (personNumber in 1..numberOfPeople) {
                thread(start = true) {
                    val database = Database(getConnection()!!)
                    val random = Random()
                    val events = database.getEvents()
                    val selectedEvent = events[random.nextInt(events.size)]
                    val areas = database.getAreas(selectedEvent.id)
                    val selectedArea = areas[random.nextInt(areas.size)]
                    val ticket = database.getTicket(selectedArea.id)
                    val person = database.getPerson("Person $personNumber")

                    database.updateTicket(person!!,ticket!!)

                    database.destroy()
                }
            }
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            System.exit(0)
        }
    }
}
