import org.postgresql.util.PSQLException
import java.sql.Connection
import java.util.*
import kotlin.collections.ArrayList


class Database (private val connection: Connection) {
    private val random = Random()

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
                    " EVENT          INT     NOT NULL REFERENCES EVENT (ID) ON DELETE CASCADE," +
                    " PRIMARY KEY(ID)" +
                    ");"
            val ticketTable = "CREATE TABLE TICKET " +
                    "(ID             INT     NOT NULL," +
                    " AVAILABLE      BOOLEAN NOT NULL, " +
                    " AREA           INT NOT NULL REFERENCES Area (id) ON DELETE CASCADE, " +
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
                    " DATE           DATE    NOT NULL, " +
                    " BUY            INT     NOT NULL REFERENCES BUY (ID) ON DELETE CASCADE, " +
                    " PRIMARY KEY(ID)" +
                    ");"
            val buyTable = "CREATE TABLE BUY " +
                    "(ID             INT     NOT NULL," +
                    " PERSON         TEXT    NOT NULL REFERENCES PERSON (NAME) ON DELETE CASCADE, " +
                    " TICKET         INT     NOT NULL REFERENCES TICKET (ID) ON DELETE CASCADE, " +
                    " DUE_DATE       DATE    NOT NULL, " +
                    " PRIMARY KEY(ID)," +
                    " UNIQUE(PERSON, TICKET)" +
                    ");"

            statement.executeUpdate(eventTable)
            statement.executeUpdate(areaTable)
            statement.executeUpdate(ticketTable)
            statement.executeUpdate(personTable)
            statement.executeUpdate(buyTable)
            statement.executeUpdate(paymentTable)

            connection.commit()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            throw e
        }
    }

    private fun dropTables() {
        val tables = listOf("Payment", "Buy", "Person","Ticket", "Area", "Event")
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

    private fun insertInitialData() {
        try {
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

    private fun insertBuy(person: Person, ticket: Ticket) {
        val insert = "INSERT INTO BUY (id, person, ticket, due_date) values (?,?,?,?);"

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH,2)

        val dueDate = calendar.time

        println(ticket.id)

        val statement = connection.prepareStatement(insert)
        statement.setInt(1, random.nextInt(100000))
        statement.setString(2, person.name)
        statement.setInt(3, ticket.id)
        statement.setDate(4, java.sql.Date(dueDate.time))

        statement.execute(insert)
        statement.closeOnCompletion()

        connection.commit()

    }

    fun create() {
        dropTables()

        createTables()
        insertInitialData()
    }

    fun destroy(){
        disconnect(connection)
    }

    fun updateTicket(person : Person, ticket: Ticket) {
        val update = "UPDATE TICKET set AVAILABLE = false where ID= ${ticket.id} AND AREA = ${ticket.area};"

        val updateStatement = connection.createStatement()
        updateStatement!!.executeUpdate(update)
        updateStatement.closeOnCompletion()

        insertBuy(person,ticket)
    }

    fun getTicket(areaId : Int): Ticket? {
        var ticket : Ticket? = null
        try {
            var available = true
            val query = "SELECT * " +
                    "FROM TICKET " +
                    "WHERE area = $areaId " +
                    "AND available = $available " +
                    "LIMIT 1 " +
                    "FOR UPDATE SKIP LOCKED;"

            val statement = connection.createStatement()
            val resultSet = statement!!.executeQuery(query)

            while (resultSet.next()) {
                val id = resultSet.getInt("id")
                val area = resultSet.getInt("area")
                val available = resultSet.getBoolean("available")
                val price = resultSet.getInt("price")

                ticket = Ticket(id, available, area, price)
            }

            resultSet.close()
            statement.closeOnCompletion()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            throw e
        }
        return ticket
    }

    fun getEvents(): ArrayList<Event> {
        val events = ArrayList<Event>()
        try {
            val query = "SELECT * FROM EVENT;"

            val statement = connection.createStatement()
            val resultSet = statement!!.executeQuery(query)

            while (resultSet.next()) {
                val id = resultSet.getInt("id")
                val name = resultSet.getString("name")
                val location = resultSet.getString("location")
                val date = resultSet.getDate("date")

                events.add(Event(id,name,location,date))
            }

            resultSet.close()
            statement.closeOnCompletion()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            System.exit(0)
        }
        return events

    }

    fun getAreas(event : Int): ArrayList<Area> {
        val areas = ArrayList<Area>()
        try {
            val query = "SELECT * FROM AREA WHERE event = $event;"

            val statement = connection.createStatement()
            val resultSet = statement!!.executeQuery(query)

            while (resultSet.next()) {
                val id = resultSet.getInt("id")
                val name = resultSet.getString("name")
                val capacity = resultSet.getInt("capacity")
                val event = resultSet.getInt("event")

                areas.add(Area(id,name,capacity,event))
            }

            resultSet.close()
            statement.closeOnCompletion()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            System.exit(0)
        }
        return areas
    }

    fun getPerson(name: String): Person? {
        var person : Person? = null
        try {
            val query = "SELECT * FROM PERSON WHERE NAME = '$name';"

            val statement = connection.createStatement()
            val resultSet = statement!!.executeQuery(query)

            while (resultSet.next()) {
                val name = resultSet.getString("name")
                val age = resultSet.getInt("age")
                val address = resultSet.getString("address")

                person = Person(name,age,address)
            }

            resultSet.close()
            statement.closeOnCompletion()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            throw e
        }
        return person
    }

    fun showTable(table : String) {
        when(table) {
            "Buy" -> {
                val buys = getBuys()
                for(buy in buys) {
                    println(buy.person)
                    println(buy.ticket)
                    println(buy.isPaid)
                    println(buy.due_date)
                }
            }
        }
    }

    private fun getBuys(): ArrayList<Buy> {
        val buys : ArrayList<Buy> = ArrayList()
        try {
            val query = "SELECT * FROM BUY;"

            val statement = connection.createStatement()
            val resultSet = statement!!.executeQuery(query)

            while (resultSet.next()) {
                val id = resultSet.getInt("id")
                val person = resultSet.getInt("person")
                val ticket = resultSet.getInt("ticket")
                val isPaid = resultSet.getBoolean("is_paid")
                val dueDate = resultSet.getDate("due_date")

                buys.add(Buy(id,person,ticket,isPaid,dueDate))
            }

            resultSet.close()
            statement.closeOnCompletion()
        } catch (e: Exception) {
            System.err.println(e.javaClass.name + ": " + e.message)
            System.exit(0)
        }
        return buys
    }

}



