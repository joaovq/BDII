import java.sql.PreparedStatement

val numberOfEvents = 3
val maxNumberOfAreasPerEvent = 5
val numberOfPeople = 100
val maxAreaCapacity = 100
val maxNumberOfTicketsPerArea = 50
val maxPriceOfTicketPerArea = 25
var paymentId = 1
var numberOfBuyTryingPerPerson = 4
var ticketsOpenTransactions = mutableMapOf<Int, PreparedStatement>()
val ticketsIdsPerEvent =  mutableMapOf<Int, ArrayList<Int>>()
