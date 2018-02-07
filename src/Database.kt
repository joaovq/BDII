import org.postgresql.util.PSQLException
import java.sql.Connection
import java.util.*
import kotlin.collections.ArrayList


class Database (private val connection: Connection) {
    fun start() {
        dropTables()

        createTables()
        insertData()
//        selectTickets()
//        selectEvents()
//        selectAreas()
//        selectPeople()
    }

    fun stop(){
        disconnect(connection)
    }

    private fun createTables() {
        try {
            val statement = connection.createStatement()
            val eventTable = "CREATE TABLE Event " +
                    "(ID              INT     NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " LOCATION       TEXT    NOT NULL, " +
                    " DATE           DATE    NOT NULL, " +
                    " PRIMARY KEY(ID)" +
                    ");"
            val areaTable = "CREATE TABLE AREA " +
                    "(ID             INT     NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " CAPACITY       INT     NOT NULL, " +
                    " EVENT          INT     NOT NULL REFERENCES EVENT (ID)," +
                    " PRIMARY KEY(ID)" +
                    ");"
            val ticketTable = "CREATE TABLE TICKET " +
                    "(ID             INT     NOT NULL," +
                    " AVAILABLE      BOOLEAN NOT NULL, " +
                    " AREA           INT NOT NULL REFERENCES Area (id), " +
                    " PRICE          INT NOT NULL, " +
                    " PRIMARY KEY(ID)" +
                    ");"
            val personTable = "CREATE TABLE PERSON " +
                    " (NAME           TEXT    NOT NULL, " +
                    " AGE            INT     NOT NULL, " +
                    " ADDRESS        TEXT    NOT NULL, " +
                    " PRIMARY KEY(NAME)" +
                    ");"
            val paymentTable = "CREATE TABLE PAYMENT " +
                    "(ID             INT     NOT NULL," +
                    " AMOUNT         INT     NOT NULL, " +
                    " DUE_DATE        TEXT    NOT NULL, " +
                    " PRIMARY KEY(ID)" +
                    ");"
            val buyTable = "CREATE TABLE BUY " +
                    "(ID             INT     NOT NULL," +
                    " PERSON         TEXT    NOT NULL REFERENCES PERSON (NAME), " +
                    " TICKET         INT     NOT NULL REFERENCES TICKET (ID), " +
                    " PAYMENT        INT     NOT NULL REFERENCES PAYMENT (ID), " +
                    " PRIMARY KEY(ID)," +
                    " UNIQUE(PERSON, TICKET)" +
                    ");"

            statement.executeUpdate(eventTable)
            statement.executeUpdate(areaTable)
            statement.executeUpdate(ticketTable)
            statement.executeUpdate(personTable)
            statement.executeUpdate(paymentTable)
            statement.executeUpdate(buyTable)

            connection.commit()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            throw e
        }
    }

    private fun dropTables() {
        val tables = listOf("Person","Ticket","Area", "Event")
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

    private fun insertData() {
        try {
            val random = Random()
            val eventIds = ArrayList<Int>()
            for (eventNumber : Int in 1..numberOfEvents) {
                val eventId = eventNumber
                val name = "Event " + eventNumber
                val location = "Location " + eventNumber
                val date = Date()
                val insert = "INSERT INTO Event (id, name, location, date) values (?,?,?,?);"

                val eventStatement = connection.prepareStatement(insert)
                eventStatement.setInt(1,eventId)
                eventStatement.setString(2,name)
                eventStatement.setString(3,location)
                eventStatement.setDate(4, java.sql.Date(date.time))

                eventStatement.executeUpdate()
                eventStatement.closeOnCompletion()

                connection.commit()

                eventIds.add(eventId)
            }

            val numberOfAreas = random.nextInt(maxNumberOfAreasPerEvent - 1) + 1
            val areasIds = ArrayList<Int>()
            for(eventId in eventIds) {
                for(areaNumber in 1..numberOfAreas) {
                    val areaId = random.nextInt(100000)
                    val name = "Area $areaNumber"
                    val capacity = random.nextInt(maxAreaCapacity - 1) + 1

                    val insert = "INSERT INTO AREA (id, name, capacity, event) values (?,?,?,?);"

                    val areaStatement = connection.prepareStatement(insert)
                    areaStatement.setInt(1,areaId)
                    areaStatement.setString(2,name)
                    areaStatement.setInt(3,capacity)
                    areaStatement.setInt(4,eventId)

                    areaStatement.executeUpdate()
                    areaStatement.closeOnCompletion()

                    connection.commit()

                    areasIds.add(areaId)
                }
            }

            val ticketsIds = ArrayList<Int>()
            for (areaId in areasIds) {
                val numberOfTickets = random.nextInt(maxNumberOfTicketsPerArea - 1) + 1
                val ticketPrice = random.nextInt(maxPriceOfTicketPerArea - (maxPriceOfTicketPerArea/2)) + maxPriceOfTicketPerArea
                for (ticketNumber in 1..numberOfTickets) {
                    var ticketId = random.nextInt(1000000)
                    while (ticketsIds.contains(ticketId))
                        ticketId = random.nextInt(1000000)
                    val insert = "INSERT INTO TICKET (id,available,area, price) values (?,?,?,?);"

                    val statament = connection.prepareStatement(insert)
                    statament.setInt(1, ticketId)
                    statament.setBoolean(2, true)
                    statament.setInt(3, areaId)
                    statament.setInt(4, ticketPrice)

                    statament.executeUpdate()
                    statament.closeOnCompletion()

                    connection.commit()

                    ticketsIds.add(ticketId)
                }
            }

            val maxAge = 75
            val minAge = 18
            for (personNumber in 1..numberOfPeople) {
                val name = "Person $personNumber"
                val age = random.nextInt(maxAge - minAge) + minAge
                val address = "Address $name"

                val insert = "INSERT INTO PERSON (name, age, address) values (?,?,?);"

                val statament = connection.prepareStatement(insert)
                statament.setString(1,name)
                statament.setInt(2, age)
                statament.setString(3, address)

                statament.executeUpdate()
                statament.closeOnCompletion()

                connection.commit()
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
                val area = tickets.getInt("area")
                val available = tickets.getBoolean("available")
                val price = tickets.getInt("price")

                println("ID = $id")
                println("AREA = $area")
                println("AVAILABLE = $available")
                println("PRICE = $price")
            }

            tickets.close()
            statement.closeOnCompletion()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            System.exit(0)
        }
    }

    private fun selectEvents() {
        try {
            val query = "SELECT * " +
                    "FROM EVENT;"

            val statement = connection.createStatement()
            val events = statement!!.executeQuery(query)

            while (events.next()) {
                val id = events.getInt("id")
                val name = events.getString("name")
                val location = events.getString("location")
                val date = events.getDate("date")

                println("ID = $id")
                println("NAME = $name")
                println("LOCATION = $location")
                println("DATE = $date")
            }

            events.close()
            statement.closeOnCompletion()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            System.exit(0)
        }
    }

    private fun selectAreas() {
        try {
            val query = "SELECT * " +
                    "FROM AREA;"

            val statement = connection.createStatement()
            val events = statement!!.executeQuery(query)

            while (events.next()) {
                val id = events.getInt("id")
                val name = events.getString("name")
                val capacity = events.getInt("capacity")
                val event = events.getInt("event")

                println("ID = $id")
                println("NAME = $name")
                println("CAPACITY = $capacity")
                println("EVENT = $event")
            }

            events.close()
            statement.closeOnCompletion()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            System.exit(0)
        }
    }

    private fun selectPeople() {
        try {
            val query = "SELECT * " +
                    "FROM PERSON;"

            val statement = connection.createStatement()
            val events = statement!!.executeQuery(query)

            while (events.next()) {
                val name = events.getString("name")
                val age = events.getInt("age")
                val address = events.getString("address")

                println("NAME = $name")
                println("AGE = $age")
                println("ADDRESS = $address")
            }

            events.close()
            statement.closeOnCompletion()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            System.exit(0)
        }
    }
}



