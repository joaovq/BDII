import java.util.*
import kotlin.concurrent.thread

class Usage() {
    fun buyTickets() {
        try {
            for (personNumber in 1..numberOfPeople) {
                thread(start = true) {
                    val random = Random()
                    val eventId = random.nextInt(2) + 1
                    val ticketsIds = ticketsIdsPerEvent[eventId]
                    val ticketId = ticketsIds!![random.nextInt(ticketsIds.size)]
                    val connection = getConnection()

                    val queryPerson = "SELECT * " +
                            "FROM PERSON " +
                            "WHERE id = " + ticketId + " " +
                            "AND event = " + eventId + " " +
                            "AND available = " + true + " " +
                            "LIMIT 1 " +
                            "FOR UPDATE SKIP LOCKED;"

                    val queryTicket = "SELECT * " +
                            "FROM TICKET " +
                            "WHERE id = " + ticketId + " " +
                            "AND event = " + eventId + " " +
                            "AND available = " + true + " " +
                            "LIMIT 1 " +
                            "FOR UPDATE SKIP LOCKED;"

                    val queryStatement = connection!!.createStatement()
                    val tickets = queryStatement.executeQuery(queryTicket)

                    while (tickets.next()) {
                        val update = "UPDATE TICKET set AVAILABLE = false where ID= ${tickets.getInt("id")} AND EVENT = ${tickets.getInt("event")};"

                        println("Trying to buy the ticket: ${tickets.getInt("id")} of event: ${tickets.getString("event")}")

                        val updateStatement = connection.createStatement()
                        updateStatement!!.executeUpdate(update)
                        updateStatement.close()
                    }
                    queryStatement.close()
                    tickets.close()
                    disconnect(connection)
                }
            }
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            System.exit(0)
        }
    }
}
