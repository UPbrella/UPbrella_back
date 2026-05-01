package upbrella.be.rent.event

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import upbrella.be.rent.entity.ConditionReport
import upbrella.be.rent.entity.ImprovementReport
import upbrella.be.rent.service.ConditionReportService
import upbrella.be.rent.service.ImprovementReportService
import upbrella.be.rent.service.RentService
import upbrella.be.slack.SlackAlarmService
import upbrella.be.slack.dto.service.input.NotifyConditionReportInput
import upbrella.be.slack.dto.service.input.NotifyImprovementReportInput
import upbrella.be.slack.dto.service.input.NotifyRentInput
import upbrella.be.slack.dto.service.input.NotifyReturnInput

@Service
class RentEventHandler(
    private val slackAlarmService: SlackAlarmService,
    private val conditionReportService: ConditionReportService,
    private val rentService: RentService,
    private val improvementReportService: ImprovementReportService,
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

    @EventListener(UmbrellaReturnedEvent::class)
    fun handleUmbrellaReturnedEvent(event: UmbrellaReturnedEvent) {

        val unrefundedRentCount = rentService.countUnrefundedRent()
        slackAlarmService.notifyReturn(
            NotifyReturnInput(
                userId = event.rentUserId,
                rentStoreName = event.rentStoreName,
                rentedAt = event.rentedAt.toString(),
                returnStoreName = event.returnStoreName,
                returnedAt = event.returnedAt.toString(),
                unrefundedCount = unrefundedRentCount
            )
        )

        event.improvementReportContent?.takeIf { it.isNotBlank() }
            ?.let { content ->
                ImprovementReport(
                    historyId = event.historyId,
                    content = content
                ).also { improvementReport ->
                    improvementReportService.save(improvementReport)
                    slackAlarmService.notifyImprovementReport(
                        NotifyImprovementReportInput(
                            umbrellaId = event.returnedUmbrellaId,
                            rentStoreName = event.rentStoreName,
                            rentedAt = event.rentedAt.toString(),
                            returnStoreName = event.returnStoreName,
                            returnedAt = event.returnedAt.toString(),
                            content = content
                        )
                    )
                }
            }
    }
}