fun main(args: Array<String>) {
    val database = Database(getConnection()!!)
    database.create()

    val usage = Usage()
    usage.buyTickets()

    database.showTable("Buy")
    database.destroy()

}