package upbrella.be.user.dto.response

data class AllHistoryResponse(
    val histories: List<SingleHistoryResponse>
) {
    companion object {
        fun of(get: List<SingleHistoryResponse>): AllHistoryResponse {
            return AllHistoryResponse(get)
        }
    }
}
