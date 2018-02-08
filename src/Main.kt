fun main(args: Array<String>) {
    val database = Database(getConnection()!!)
    database.create()

    println("Number of available tickets: ${database.getNumberOfAvailableTickets()}")

    val usage = Usage()
    usage.sellTickets()

    println("Number of available tickets: ${database.getNumberOfAvailableTickets()}")

    database.destroy()

}