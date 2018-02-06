import org.postgresql.util.PSQLException
import java.sql.Connection
import java.util.*


class Database (private val connection: Connection) {
    fun start() {
        dropTables()

        createTables()
        insertData()
        //selectTickets()
    }

    fun stop(){
        disconnect(connection)
    }

    private fun createTables() {
        try {
            val statement = connection.createStatement()
            val eventTable = "CREATE TABLE Event " +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " CAPACITY       INT     NOT NULL " +
                    ");"
            val ticketTable = "CREATE TABLE TICKET " +
                    "(ID INT NOT NULL," +
                    " AVAILABLE BOOLEAN NOT NULL, " +
                    " EVENT INT NOT NULL REFERENCES Event (id), " +
                    "CONSTRAINT PK_Ticket PRIMARY KEY (id, event) " +
                    ");"

            statement.executeUpdate(eventTable)
            statement.executeUpdate(ticketTable)

            connection.commit()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            throw e
        }
    }

    private fun insertData() {
        try {
            val statement = connection.createStatement()

            val random = Random()
            val maxEventCapacity = 20
            for (id : Int in 1..numberOfEvents) {
                val name = "Event " + id
                val capacity = random.nextInt(maxEventCapacity - 1) + 1
                val insert = "INSERT INTO Event " +
                        "values ("+id+"," + "'" + name + "'" + "," + capacity + ");"

                statement.executeUpdate(insert)
                connection.commit()
            }

            for (eventId : Int in 1..numberOfEvents) {
                for (ticketId: Int in 1..numberOfTickets) {
                    val insert = "INSERT INTO Ticket " +
                            "values ("+ticketId+"," + true + "," + eventId + ");"
                    statement.executeUpdate(insert)
                    connection.commit()
                }
            }
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            throw e
        }
    }

    private fun selectTickets() {
        try {
            val query = "SELECT * " +
                    "FROM TICKET;"

            val statement = connection.createStatement()
            val tickets = statement!!.executeQuery(query)

            while (tickets.next()) {
                val id = tickets.getInt("id")
                val event = tickets.getInt("event")
                val available = tickets.getBoolean("available")

                println("ID = $id")
                println("EVENT = $event")
                println("AVAILABLE = $available")
            }

            tickets.close()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            System.exit(0)
        }
    }

    private fun dropTables() {
        val tables = listOf("Ticket", "Event")
        for(table in tables) {
            val statement = connection.createStatement()
            try {
                val sqlDrop = "DROP TABLE $table"
                statement.executeUpdate(sqlDrop)
            } catch (e: PSQLException) {
                statement.close()
            }
            connection.commit()
        }
    }
}



