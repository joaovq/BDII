fun main(args: Array<String>) {
    val database = Database(getConnection()!!)
    database.create()

    sellTickets()

    //database.countNumberOfMen()
    database.numberOfTicketSoldToSomeone(89)
    database.areaCapacity(2)
    database.eventsLocation(2)
    database.mostExpensiveTicketOfEvent(1)
    database.sumOfTicketsSoldInEvent(1)
    database.numberOfAreasOfEvent(2)
    database.mostExpensiveTicketSold()

    database.destroy()

}