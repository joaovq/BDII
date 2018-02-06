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
        generateTicketsIds()
        try {
            val random = Random()
            val maxEventCapacity = 20
            for (id : Int in 1..numberOfEvents) {
                val name = "Event " + id
                val capacity = random.nextInt(maxEventCapacity - 1) + 1
                val insert = "INSERT INTO Event (id,name,capacity) values (?,?,?);"

                val statament = connection.prepareStatement(insert)
                statament.setInt(1,id)
                statament.setString(2,name)
                statament.setInt(3,capacity)

                statament.executeUpdate()
                statament.closeOnCompletion()

                connection.commit()
            }

            for ((eventId, ticketsIds) in ticketsIdsPerEvent) {
                for (ticketId in ticketsIds) {
                    val insert = "INSERT INTO TICKET (id,available,event) values (?,?,?);"

                    val statament = connection.prepareStatement(insert)
                    statament.setInt(1, ticketId)
                    statament.setBoolean(2, true)
                    statament.setInt(3, eventId)

                    statament.executeUpdate()
                    statament.closeOnCompletion()

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



