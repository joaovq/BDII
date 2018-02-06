import java.sql.Connection
import java.sql.DriverManager

fun getConnection(): Connection? {
    var connection : Connection? = null
    try {
        Class.forName("org.postgresql.Driver")
        connection  = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/event",
                        "postgres", "088519")
        connection!!.autoCommit = false

        return connection
    } catch (e: Exception) {
        System.err.println(e.javaClass.name + ": " + e.message)
        System.exit(0)
    }

    return connection
}

fun disconnect(connection : Connection){
    try {
        connection.close()
    } catch (e: Exception) {
        System.err.println(e.javaClass.name + ": " + e.message)
        System.exit(0)
    }

    println("Operation done successfully")
}