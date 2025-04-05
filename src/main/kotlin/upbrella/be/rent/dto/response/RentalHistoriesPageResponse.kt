package upbrella.be.rent.dto.response

data class RentalHistoriesPageResponse(
    val rentalHistoryResponsePage: List<RentalHistoryResponse>,
    val countOfAllHistories: Long,
    val countOfAllPages: Long
) {
    companion object {
        fun of(
            rentalHistories: List<RentalHistoryResponse>,
            countOfAllHistories: Long,
            countOfAllPages: Long
        ): RentalHistoriesPageResponse {
            return RentalHistoriesPageResponse(
                rentalHistoryResponsePage = rentalHistories,
                countOfAllHistories = countOfAllHistories,
                countOfAllPages = countOfAllPages
            )
        }
    }
}
