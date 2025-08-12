package upbrella.be.rent.event

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import upbrella.be.rent.entity.ConditionReport
import upbrella.be.rent.service.ConditionReportService
import upbrella.be.slack.SlackAlarmService
import upbrella.be.slack.dto.service.input.NotifyConditionReportInput
import upbrella.be.slack.dto.service.input.NotifyRentInput

@Service
class RentEventHandler(
    private val slackAlarmService: SlackAlarmService,
    private val conditionReportService: ConditionReportService,
) {

    @EventListener(UmbrellaRentedEvent::class)
    fun handleUmbrellaRentedEvent(event: UmbrellaRentedEvent) {
        slackAlarmService.notifyRent(
            NotifyRentInput(
                userId = event.userId,
                userName = event.userName,
                rentStoreName = event.rentStoreName
            )
        )

        event.conditionReportContent
            ?.takeIf { it.isNotBlank() }
            ?.let { content ->
                ConditionReport(
                    historyId = event.historyId,
                    content = content
                ).also { conditionReport ->
                    conditionReportService.saveConditionReport(conditionReport)
                    slackAlarmService.notifyConditionReport(
                        NotifyConditionReportInput(
                            umbrellaId = event.umbrellaId,
                            rentStoreName = event.rentStoreName,
                            content = content
                        )
                    )
                }
            }
    }
}