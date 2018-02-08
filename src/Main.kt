fun main(args: Array<String>) {
    val database = Database(getConnection()!!)
    database.create()

    println("Number of available tickets: ${database.getNumberOfAvailableTickets()}")

    sellTickets()

    //database.countNumberOfMen()
    database.numberOfTicketSoldToSomeone("89")

    println("Number of available tickets: ${database.getNumberOfAvailableTickets()}")

    database.destroy()

}