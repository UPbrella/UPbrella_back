package upbrella.be.umbrella.dto.response

data class UmbrellaStatisticsResponse(
    val totalUmbrellaCount: Long,
    val rentableUmbrellaCount: Long,
    val rentedUmbrellaCount: Long,
    val missingUmbrellaCount: Long,
    val missingRate: Double,
    val totalRentCount: Long
) {
    companion object {
        fun fromCounts(
            totalUmbrellaCount: Long,
            rentableUmbrellaCount: Long,
            rentedUmbrellaCount: Long,
            missingUmbrellaCount: Long,
            totalRentCount: Long
        ): UmbrellaStatisticsResponse {
            val missingRate = if (totalUmbrellaCount != 0L) {
                100.0 * missingUmbrellaCount / totalUmbrellaCount
            } else {
                0.0
            }

            return UmbrellaStatisticsResponse(
                totalUmbrellaCount = totalUmbrellaCount,
                rentableUmbrellaCount = rentableUmbrellaCount,
                rentedUmbrellaCount = rentedUmbrellaCount,
                missingUmbrellaCount = missingUmbrellaCount,
                missingRate = missingRate,
                totalRentCount = totalRentCount
            )
        }
    }
}