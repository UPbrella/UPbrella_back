package upbrella.be.user.dto.response

import upbrella.be.user.entity.BlackList

data class AllBlackListResponse(
    val blackList: List<SingleBlackListResponse>
) {
    companion object {
        fun of(blackList: List<BlackList>): AllBlackListResponse =
            AllBlackListResponse(blackList.map { SingleBlackListResponse.of(it) })
    }
}
