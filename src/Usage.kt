import java.sql.Connection
import kotlin.concurrent.thread

class Usage() {
    fun buyTickets() {
        try {
            for (index in 1..numberOfThreads) {
                thread(start = true) {
                    val connection = getConnection()
                    for (eventId: Int in 1..numberOfEvents) {
                        for (ticketId: Int in 1..numberOfTickets) {
                            val query = "SELECT * " +
                                    "FROM TICKET " +
                                    "WHERE id = " + ticketId + " " +
                                    "AND event = " + eventId + " " +
                                    "AND available = " + true + " " +
                                    "LIMIT 1 " +
                                    "FOR UPDATE SKIP LOCKED;"

                            val queryStatement = connection!!.createStatement()
                            val tickets = queryStatement.executeQuery(query)

                            while (tickets.next()) {
                                val update = "UPDATE TICKET set AVAILABLE = false where ID= ${tickets.getInt("id")} AND EVENT = ${tickets.getInt("event")};"

                                println("Trying to buy the ticket: ${tickets.getInt("id")} of event: ${tickets.getString("event")}")

                                val updateStatement = connection.createStatement()
                                updateStatement!!.executeUpdate(update)
                                updateStatement.close()
                            }
                            queryStatement.close()
                            tickets.close()
                        }
                    }
                    disconnect(connection!!)
                }
            }
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            System.exit(0)
        }
    }
}
