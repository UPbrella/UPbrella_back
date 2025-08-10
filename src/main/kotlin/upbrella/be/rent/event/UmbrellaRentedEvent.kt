package upbrella.be.rent.event

import upbrella.be.rent.entity.History
import upbrella.be.util.event.Event

class UmbrellaRentedEvent(
    val userId: Long,
    val userName: String,
    val rentStoreName: String,
    val conditionReportContent: String? = null,
    val umbrellaId: Long,
    val history: History,
) : Event()
