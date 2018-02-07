fun main(args: Array<String>) {
    val database = Database(getConnection()!!)

    database.start()

//    val usage = Usage()

//    usage.buyTickets()

    database.stop()
}