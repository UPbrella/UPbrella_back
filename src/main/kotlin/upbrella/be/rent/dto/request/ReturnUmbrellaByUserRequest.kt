package upbrella.be.rent.dto.request

import javax.validation.constraints.Size

data class ReturnUmbrellaByUserRequest(
    val returnStoreId: Long = 0,
    val bank: String? = null,
    val accountNumber: String? = null,

    @field:Size(max = 400, message = "최대 400자여야 합니다.")
    val improvementReportContent: String? = null
)