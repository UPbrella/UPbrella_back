package upbrella.be.config

import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import upbrella.be.store.entity.*
import upbrella.be.store.repository.*
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest
import upbrella.be.umbrella.repository.UmbrellaRepository
import java.time.DayOfWeek
import java.time.LocalTime

@Component
@Profile("home-dev")
class DevelopmentSeed(private val stores: StoreMetaRepository, private val details: StoreDetailRepository,
    private val classes: ClassificationRepository, private val hours: BusinessHourRepository,
    private val umbrellas: UmbrellaRepository) {
    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun initialize() {
        if (stores.count() != 0L) return
        val area = classes.save(Classification(type = ClassificationType.CLASSIFICATION, name = "개발 테스트 지역", latitude = 37.558, longitude = 126.936))
        val sub = classes.save(Classification(type = ClassificationType.SUB_CLASSIFICATION, name = "테스트 지점"))
        repeat(2) { n ->
            val store = stores.save(StoreMeta(name = "개발 대여소 ${n + 1}", activated = true, classification = area,
                subClassification = sub, category = "테스트", latitude = 37.558 + n * 0.001, longitude = 126.936))
            details.save(StoreDetail(storeMeta = store, umbrellaLocation = "입구 테스트 보관함", workingHour = "매일 00:00–23:59",
                address = "개발용 가상 지점", content = "실제 운영 지점이 아닙니다. 대여·반납 기능 확인용입니다."))
            DayOfWeek.values().forEach { day -> hours.save(BusinessHour(storeMeta = store, date = day, openAt = LocalTime.MIDNIGHT, closeAt = LocalTime.of(23,59))) }
            repeat(5) { i -> umbrellas.save(Umbrella.ofCreated(UmbrellaCreateRequest(
                storeMetaId = store.id!!, uuid = 260001L + n * 5 + i, rentable = true,
                etc = "개발 테스트 우산"), store)) }
        }
    }
}
