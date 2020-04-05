package io.allune.taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
        allDrivers.filter { driver -> trips.count { trip -> trip.driver == driver } == 0 }.toSet()

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
        allPassengers.filter { passenger -> trips.count { trip -> passenger in trip.passengers } >= minTrips }.toSet()

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
        allPassengers.filter { passenger -> trips.count { trip -> passenger in trip.passengers && trip.driver == driver } > 1 }.toSet()

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> =
        allPassengers.filter { passenger ->
            trips.count { trip -> passenger in trip.passengers && trip.discount != null } >
                    trips.count { trip -> passenger in trip.passengers && trip.discount == null }
        }.toSet()

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    if (trips.isEmpty()) return null

    // Find the maximum trip duration
    val maxDuration: Int = trips.map { it.duration }.max() ?: 0
    val tripFrequencyByRange = HashMap<Int, IntRange>()

    (0..maxDuration step 10).forEach { rangeStart ->
        // Create the range and count the trips which duration is within the range
        val currentRange = IntRange(rangeStart, rangeStart + 9)
        val tripsInCurrentRange = trips.filter { trip -> trip.duration in currentRange }.count()
        tripFrequencyByRange[tripsInCurrentRange] = currentRange
    }

    // Sort the result and get the range with highest frequency
    return tripFrequencyByRange[tripFrequencyByRange.toSortedMap().lastKey()]

}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    if (this.trips.isEmpty()) return false

    // Find the cost of all trips
    val totalTripsCost = this.trips.map { it.cost }.sum()
    // Find the sum of all trips cost for each driver
    val driverToCostPairs = trips
            .groupBy { it.driver }
            .mapValues { (_, trips) -> trips.sumByDouble { it.cost } }
            .toList()
    // Convert to map sorted by cost
    val tripCostByDriver = driverToCostPairs
            .sortedByDescending { (_, value) -> value }
            .toMap()


    val costForBestPerformers = totalTripsCost * 0.8
    var currentAccumulatedCost = 0.0
    var numberOfDrivers = 0
    // Iterate through the map of drivers and trip costs and add the cost for each driver
    // Stop the count when the accumulated cost has reached over 80% of the total trips cost
    for (value in tripCostByDriver.values) {
        numberOfDrivers++
        currentAccumulatedCost += value
        if (currentAccumulatedCost >= costForBestPerformers) break
    }

    // Check if the number of drivers calculated are the best performers
    return numberOfDrivers <= (allDrivers.size * 0.2)

}