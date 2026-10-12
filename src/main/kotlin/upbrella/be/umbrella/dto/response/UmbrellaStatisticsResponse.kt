package upbrella.be.umbrella.dto.response

import upbrella.be.umbrella.entity.UmbrellaStatus

data class UmbrellaStatisticsResponse(
    val totalUmbrellaCount: Long,
    val rentableUmbrellaCount: Long, // 사용 가능
    val rentedUmbrellaCount: Long, // 대여중
    val unlocatedUmbrellaCount: Long, // 위치 미확인
    val lostUmbrellaCount: Long, // 분실
    val missingUmbrellaCount: Long, // 위치 미확인 + 분실 (기존 필드)
    val missingRate: Double,
    val totalRentCount: Long
) {
    companion object {
        fun of(countByStatus: Map<UmbrellaStatus, Long>, totalRentCount: Long): UmbrellaStatisticsResponse {
            val totalUmbrellaCount = countByStatus.values.sum()
            val unlocatedUmbrellaCount = countByStatus[UmbrellaStatus.UNLOCATED] ?: 0L
            val lostUmbrellaCount = countByStatus[UmbrellaStatus.LOST] ?: 0L
            val missingUmbrellaCount = unlocatedUmbrellaCount + lostUmbrellaCount
            val missingRate = if (totalUmbrellaCount != 0L) {
                100.0 * missingUmbrellaCount / totalUmbrellaCount
            } else {
                0.0
            }

            return UmbrellaStatisticsResponse(
                totalUmbrellaCount = totalUmbrellaCount,
                rentableUmbrellaCount = countByStatus[UmbrellaStatus.AVAILABLE] ?: 0L,
                rentedUmbrellaCount = countByStatus[UmbrellaStatus.RENTED] ?: 0L,
                unlocatedUmbrellaCount = unlocatedUmbrellaCount,
                lostUmbrellaCount = lostUmbrellaCount,
                missingUmbrellaCount = missingUmbrellaCount,
                missingRate = missingRate,
                totalRentCount = totalRentCount
            )
        }
    }
}
