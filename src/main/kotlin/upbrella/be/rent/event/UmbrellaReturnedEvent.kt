package upbrella.be.rent.event

import upbrella.be.util.event.Event
import java.time.LocalDateTime

class UmbrellaReturnedEvent(
    val rentUserId: Long,
    val rentStoreName: String,
    val rentedAt: LocalDateTime,
    val returnStoreName: String,
    val returnedAt: LocalDateTime,
    val historyId: Long,
    val improvementReportContent: String? = null,
    val returnedUmbrellaId: Long,
) : Event()
