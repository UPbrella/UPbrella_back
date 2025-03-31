package upbrella.be.store.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.times
import upbrella.be.store.dto.request.SingleBusinessHourRequest
import upbrella.be.store.dto.response.SingleBusinessHourResponse
import upbrella.be.store.entity.BusinessHour
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.repository.BusinessHourRepository
import java.time.DayOfWeek
import java.time.LocalTime

@ExtendWith(MockitoExtension::class)
class BusinessHourServiceTest {

    @Mock
    private lateinit var businessHourRepository: BusinessHourRepository

    @InjectMocks
    private lateinit var businessHourService: BusinessHourService

    @Test
    @DisplayName("saveAllBusinessHour() 호출하면, 모든 요일이 저장된다.")
    fun saveAllBusinessHour() {
        // given
        val monday = BusinessHour.builder()
            .date(DayOfWeek.MONDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val tuesday = BusinessHour.builder()
            .date(DayOfWeek.TUESDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val wednesday = BusinessHour.builder()
            .date(DayOfWeek.WEDNESDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val thursday = BusinessHour.builder()
            .date(DayOfWeek.THURSDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val friday = BusinessHour.builder()
            .date(DayOfWeek.FRIDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val saturday = BusinessHour.builder()
            .date(DayOfWeek.SATURDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val sunday = BusinessHour.builder()
            .date(DayOfWeek.SUNDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()

        val businessHours = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)

        // when
        businessHourService.saveAllBusinessHour(businessHours)

        // then
        then(businessHourRepository).should(times(1)).saveAll(businessHours)
    }

    @Test
    @DisplayName("id 를 기준으로 조회하면 모든 요일이 조회된다.")
    fun findBusinessHourByStoreMetaIdTest() {
        // given
        val monday = BusinessHour.builder()
            .date(DayOfWeek.MONDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val tuesday = BusinessHour.builder()
            .date(DayOfWeek.TUESDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val wednesday = BusinessHour.builder()
            .date(DayOfWeek.WEDNESDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val thursday = BusinessHour.builder()
            .date(DayOfWeek.THURSDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val friday = BusinessHour.builder()
            .date(DayOfWeek.FRIDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val saturday = BusinessHour.builder()
            .date(DayOfWeek.SATURDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val sunday = BusinessHour.builder()
            .date(DayOfWeek.SUNDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()

        val businessHours = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)

        given(businessHourRepository.findByStoreMetaId(1L)).willReturn(businessHours)

        // when
        val businessHourList = businessHourService.findBusinessHourByStoreMetaId(1L)

        // then
        assertAll(
            { assertThat(businessHourList).hasSize(7) },
            { assertThat(businessHourList).containsAll(businessHours) }
        )
    }

    @Test
    @DisplayName("id를 기준으로 조회했는데 id가 없으면 빈 리스트가 조회된다.")
    fun emptyBusinessTest() {
        // given
        given(businessHourRepository.findByStoreMetaId(1L)).willReturn(listOf())

        // when
        val businessHours = businessHourService.findBusinessHourByStoreMetaId(1L)

        // then
        assertThat(businessHours).isEmpty()
    }

    @Test
    @DisplayName("협업지점의 영업시간을 id 를 기준으로 수정할 수 있다.")
    fun updateBusinessHourTest() {
        // given
        val businessHours = mutableListOf<BusinessHour>()
        val updateBusinessHours = mutableListOf<SingleBusinessHourRequest>()

        // 이전 데이터와 업데이트 데이터를 생성
        for (day in DayOfWeek.values()) {
            businessHours.add(
                BusinessHour.builder()
                    .date(day)
                    .openAt(LocalTime.of(9, 0))
                    .closeAt(LocalTime.of(18, 0))
                    .build()
            )
            updateBusinessHours.add(
                SingleBusinessHourRequest.builder()
                    .date(day)
                    .openAt(LocalTime.of(10, 10))
                    .closeAt(LocalTime.of(19, 10))
                    .build()
            )
        }

        val storeMeta = StoreMeta(
            id = 1L,
            activated = false,
            category = "category",
            businessHours = businessHours,
            name = "협업지점명",
        )

        given(businessHourRepository.findByStoreMetaId(1L))
            .willReturn(
                updateBusinessHours.map {
                    BusinessHour.ofCreateBusinessHour(it, storeMeta)
                }
            )

        // when
        businessHourService.updateBusinessHours(storeMeta, updateBusinessHours)
        val foundStore = businessHourRepository.findByStoreMetaId(1L)

        // then
        for (i in businessHours.indices) {
            val original = foundStore[i]
            val updated = updateBusinessHours[i]

            assertAll(
                { assertThat(original.openAt).isEqualTo(updated.openAt) },
                { assertThat(original.closeAt).isEqualTo(updated.closeAt) }
            )
        }
    }

    @Test
    @DisplayName("협업지점 응답을 위해 entity를 dto로 변경할 수 있다.")
    fun createBusinessHourResponseTest() {
        // given
        val monday = BusinessHour.builder()
            .date(DayOfWeek.MONDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val tuesday = BusinessHour.builder()
            .date(DayOfWeek.TUESDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val wednesday = BusinessHour.builder()
            .date(DayOfWeek.WEDNESDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val thursday = BusinessHour.builder()
            .date(DayOfWeek.THURSDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val friday = BusinessHour.builder()
            .date(DayOfWeek.FRIDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val saturday = BusinessHour.builder()
            .date(DayOfWeek.SATURDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val sunday = BusinessHour.builder()
            .date(DayOfWeek.SUNDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()

        val responseMonday = SingleBusinessHourResponse.builder()
            .date(DayOfWeek.MONDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()

        val responseTuesday = SingleBusinessHourResponse.builder()
            .date(DayOfWeek.TUESDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()

        val responseWednesday = SingleBusinessHourResponse.builder()
            .date(DayOfWeek.WEDNESDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()

        val responseThursday = SingleBusinessHourResponse.builder()
            .date(DayOfWeek.THURSDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()

        val responseFriday = SingleBusinessHourResponse.builder()
            .date(DayOfWeek.FRIDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()

        val responseSaturday = SingleBusinessHourResponse.builder()
            .date(DayOfWeek.SATURDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()

        val responseSunday = SingleBusinessHourResponse.builder()
            .date(DayOfWeek.SUNDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()

        val businessHours = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)
        val businessHourResponses = listOf(
            responseMonday, responseTuesday, responseWednesday,
            responseThursday, responseFriday, responseSaturday, responseSunday
        )

        // when
        val response = businessHourService.createBusinessHourResponse(businessHours)

        // then
        assertAll(
            { assertThat(response.size).isEqualTo(businessHourResponses.size) },
            { assertThat(response[0].date).isEqualTo(businessHourResponses[0].date) },
            { assertThat(response[1].date).isEqualTo(businessHourResponses[1].date) },
            { assertThat(response[2].date).isEqualTo(businessHourResponses[2].date) },
            { assertThat(response[3].date).isEqualTo(businessHourResponses[3].date) },
            { assertThat(response[4].date).isEqualTo(businessHourResponses[4].date) },
            { assertThat(response[5].date).isEqualTo(businessHourResponses[5].date) },
            { assertThat(response[6].date).isEqualTo(businessHourResponses[6].date) }
        )
    }
}
