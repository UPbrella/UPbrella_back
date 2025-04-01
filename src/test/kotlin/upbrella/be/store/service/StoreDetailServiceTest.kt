package upbrella.be.store.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.BDDMockito.*
import org.mockito.Mockito.times
import upbrella.be.store.dto.request.SingleBusinessHourRequest
import upbrella.be.store.dto.request.UpdateStoreRequest
import upbrella.be.store.dto.response.*
import upbrella.be.store.entity.*
import upbrella.be.store.exception.NonExistingStoreDetailException
import upbrella.be.store.repository.StoreDetailRepository
import upbrella.be.umbrella.service.UmbrellaService
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class StoreDetailServiceTest {

    @Mock
    private lateinit var classificationService: ClassificationService

    @Mock
    private lateinit var storeMetaService: StoreMetaService

    @Mock
    private lateinit var umbrellaService: UmbrellaService

    @Mock
    private lateinit var storeDetailRepository: StoreDetailRepository

    @Mock
    private lateinit var businessHourService: BusinessHourService

    @Mock
    private lateinit var storeImageService: StoreImageService

    @InjectMocks
    private lateinit var storeDetailService: StoreDetailService

    @Nested
    @DisplayName("협업 지점의 고유번호를 입력받아")
    inner class FindStoreDetailByStoreMetaIdTest {

        val monday = BusinessHour(
            date = DayOfWeek.MONDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val tuesday = BusinessHour(
            date = DayOfWeek.TUESDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )

        private val businessHours = listOf(monday, tuesday)

        private val storeMeta = StoreMeta(
            id = 3L,
            name = "스타벅스",
            deleted = false,
            latitude = 37.503716,
            longitude = 127.053718,
            activated = true,
            category = "카페, 디저트",
            businessHours = businessHours,
        )

        private val storeDetail = StoreDetail(
            id = 2L,
            storeMeta = storeMeta,
            content = "모티브 카페 소개",
            address = "모티브로 32길",
            contactInfo = "010-5252-8282",
            instaUrl = "모티브 인서타",
            workingHour = "매일 7시 ~ 12시",
            umbrellaLocation = "문 앞",
            storeImages = listOf(),
        )

        private val storeFindByIdResponseExpected =
            StoreFindByIdResponse.fromStoreDetail(storeDetail, 10L)

        @Test
        @DisplayName("해당하는 협업 지점의 정보를 성공적으로 반환한다.")
        fun success() {
            // given
            given(storeDetailRepository.findByStoreMetaIdUsingFetchJoin(3L))
                .willReturn(Optional.of(storeDetail))
            given(umbrellaService.countAvailableUmbrellaAtStore(3L))
                .willReturn(10L)

            // when
            val storeFindByIdResponse = storeDetailService.findStoreDetailByStoreId(3L)

            // then
            assertAll(
                {
                    assertThat(storeFindByIdResponse)
                        .usingRecursiveComparison()
                        .isEqualTo(storeFindByIdResponseExpected)
                },
                {
                    then(storeDetailRepository).should(times(1))
                        .findByStoreMetaIdUsingFetchJoin(3L)
                },
                {
                    then(umbrellaService).should(times(1))
                        .countAvailableUmbrellaAtStore(3L)
                }
            )
        }
    }

    @Nested
    @DisplayName("사용자는 ")
    inner class StoreDetailNestedTest {
        val monday = BusinessHour(
            date = DayOfWeek.MONDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val tuesday = BusinessHour(
            date = DayOfWeek.TUESDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val wednesday = BusinessHour(
            date = DayOfWeek.WEDNESDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val thursday = BusinessHour(
            date = DayOfWeek.THURSDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val friday = BusinessHour(
            date = DayOfWeek.FRIDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val saturday = BusinessHour(
            date = DayOfWeek.SATURDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val sunday = BusinessHour(
            date = DayOfWeek.SUNDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )

        private val classification = Classification.builder()
            .id(1L)
            .type(ClassificationType.CLASSIFICATION)
            .name("대분류")
            .latitude(33.33)
            .longitude(33.33)
            .build()

        private val subClassification = Classification.builder()
            .id(2L)
            .type(ClassificationType.SUB_CLASSIFICATION)
            .name("소분류")
            .build()

        private val businessHours =
            listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)

        private val storeMeta = StoreMeta(
            id = 1L,
            name = "협업 지점명",
            activated = true,
            deleted = false,
            classification = classification,
            subClassification = subClassification,
            category = "카테고리",
            latitude = 33.33,
            longitude = 33.33,
            businessHours = businessHours
        )

        private val first = StoreImage(
            id = 1L,
            imageUrl = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg"
        )

        private val second = StoreImage(
            id = 2L,
            imageUrl = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg"
        )

        private val images = setOf(first, second)

        private val singleStoreResponse = SingleStoreResponse.builder()
            .id(1L)
            .name("협업 지점명")
            .activateStatus(true)
            .classification(
                SingleClassificationResponse.builder()
                    .id(1L)
                    .name("대분류")
                    .type(ClassificationType.CLASSIFICATION)
                    .latitude(33.33)
                    .longitude(33.33)
                    .build()
            )
            .subClassification(
                SingleSubClassificationResponse.builder()
                    .id(2L)
                    .type(ClassificationType.SUB_CLASSIFICATION)
                    .name("소분류")
                    .build()
            )
            .category("카테고리")
            .latitude(33.33)
            .longitude(33.33)
            .umbrellaLocation("우산 위치")
            .businessHour("근무 시간")
            .instagramId("인스타그램 주소")
            .contactNumber("연락처")
            .address("주소")
            .addressDetail("상세 주소")
            .content("내용")
            .build()

        @Test
        @DisplayName("모든 협업 지점의 정보를 조회할 수 있다.")
        fun findAllTest() {
            // given
            given(storeDetailRepository.findAllStoresForAdmin())
                .willReturn(listOf(singleStoreResponse))

            val expected = SingleStoreResponse.builder()
                .id(1L)
                .name("협업 지점명")
                .activateStatus(true)
                .classification(
                    SingleClassificationResponse.builder()
                        .id(1L)
                        .name("대분류")
                        .type(ClassificationType.CLASSIFICATION)
                        .latitude(33.33)
                        .longitude(33.33)
                        .build()
                )
                .subClassification(
                    SingleSubClassificationResponse.builder()
                        .id(2L)
                        .type(ClassificationType.SUB_CLASSIFICATION)
                        .name("소분류")
                        .build()
                )
                .category("카테고리")
                .latitude(33.33)
                .longitude(33.33)
                .umbrellaLocation("우산 위치")
                .businessHour("근무 시간")
                .instagramId("인스타그램 주소")
                .contactNumber("연락처")
                .address("주소")
                .addressDetail("상세 주소")
                .content("내용")
                .build()

            // when
            val allStores = storeDetailService.findAllStores()

            // then
            assertAll(
                { assertThat(allStores).hasSize(1) },
                {
                    assertThat(allStores[0])
                        .usingRecursiveComparison()
                        .isEqualTo(expected)
                }
            )
        }
    }

    @Nested
    @DisplayName("사용자는 협업지점 상세정보를 ")
    inner class FindStoreDetail {
        val monday = BusinessHour(
            date = DayOfWeek.MONDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val tuesday = BusinessHour(
            date = DayOfWeek.TUESDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val wednesday = BusinessHour(
            date = DayOfWeek.WEDNESDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val thursday = BusinessHour(
            date = DayOfWeek.THURSDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val friday = BusinessHour(
            date = DayOfWeek.FRIDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val saturday = BusinessHour(
            date = DayOfWeek.SATURDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val sunday = BusinessHour(
            date = DayOfWeek.SUNDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        
        private val classification = Classification.builder()
            .id(1L)
            .type(ClassificationType.CLASSIFICATION)
            .name("대분류")
            .latitude(33.33)
            .longitude(33.33)
            .build()

        private val subClassification = Classification.builder()
            .id(2L)
            .type(ClassificationType.SUB_CLASSIFICATION)
            .name("소분류")
            .build()

        private val businessHours =
            listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)

        private val storeMeta = StoreMeta(
            id = 1L,
            name = "협업 지점명",
            activated = true,
            deleted = false,
            classification = classification,
            subClassification = subClassification,
            category = "카테고리",
            latitude = 33.33,
            longitude = 33.33,
            businessHours = businessHours
        )

        private val first = StoreImage(
            id = 1L,
            imageUrl = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg"
        )

        private val second = StoreImage(
            id = 2L,
            imageUrl = "https://null.s3.ap-northeast-2.amazonaws.com/store-image/filename.jpg"
        )

        private val images = listOf(first, second)

        private val storeDetail = StoreDetail(
            id = 1L,
            storeMeta = storeMeta,
            umbrellaLocation = "우산 위치",
            workingHour = "근무 시간",
            instaUrl = "인스타그램 주소",
            contactInfo = "연락처",
            address = "주소",
            addressDetail = "상세 주소",
            content = "내용",
            storeImages = images
        )

        @Test
        @DisplayName("id로 조회할 수 있다.")
        fun findByIdTest() {
            // given
            val storeMetaId = 1L
            given(storeDetailRepository.findByStoreMetaIdUsingFetchJoin(storeMetaId))
                .willReturn(Optional.of(storeDetail))

            // when
            val storeDetailById = storeDetailService.findStoreDetailByStoreMetaId(storeMetaId)

            // then
            assertAll(
                { assertThat(storeDetailById).isNotNull() },
                {
                    assertThat(storeDetailById)
                        .usingRecursiveComparison()
                        .isEqualTo(storeDetail)
                }
            )
        }

        @Test
        @DisplayName("id로 조회하는데 없을 경우 예외를 발생시킨다.")
        fun notFoundException() {
            // given
            val storeDetailId = 1L

            // when & then
            assertThatThrownBy {
                storeDetailService.findStoreDetailByStoreMetaId(storeDetailId)
            }
                .isInstanceOf(NonExistingStoreDetailException::class.java)
                .hasMessageContaining("[ERROR] 존재하지 않는 가게입니다.")
        }
    }

    @Test
    @DisplayName("사용자는 협업지점을 수정할 수 있다.")
    fun updateStoreTest() {
        // given
        val storeId = 1L

        val monday = BusinessHour(
            date = DayOfWeek.MONDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val tuesday = BusinessHour(
            date = DayOfWeek.TUESDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val wednesday = BusinessHour(
            date = DayOfWeek.WEDNESDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val thursday = BusinessHour(
            date = DayOfWeek.THURSDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val friday = BusinessHour(
            date = DayOfWeek.FRIDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val saturday = BusinessHour(
            date = DayOfWeek.SATURDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )
        val sunday = BusinessHour(
            date = DayOfWeek.SUNDAY,
            openAt = LocalTime.of(9, 0),
            closeAt = LocalTime.of(18, 0)
        )

        val businessHours = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday)

        val classification = Classification.builder()
            .id(1L)
            .type(ClassificationType.CLASSIFICATION)
            .name("대분류")
            .latitude(33.33)
            .longitude(33.33)
            .build()

        val subClassification = Classification.builder()
            .id(2L)
            .type(ClassificationType.SUB_CLASSIFICATION)
            .name("소분류")
            .build()

        val storeMeta = StoreMeta(
            id = 1L,
            name = "협업 지점명",
            activated = true,
            deleted = false,
            classification = classification,
            subClassification = subClassification,
            category = "카테고리",
            latitude = 33.33,
            longitude = 33.33,
            businessHours = businessHours
        )

        val storeDetail = StoreDetail(
            id = storeId,
            storeMeta = storeMeta,
            umbrellaLocation = "우산 위치",
            workingHour = "근무 시간",
            instaUrl = "인스타그램 주소",
            contactInfo = "연락처",
            address = "주소",
            addressDetail = "상세 주소",
            content = "내용",
            storeImages = listOf(StoreImage(id = 1L, imageUrl = "가게 썸네일"))
        )

        val mondayUpdate = SingleBusinessHourRequest.builder()
            .date(DayOfWeek.MONDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val tuesdayUpdate = SingleBusinessHourRequest.builder()
            .date(DayOfWeek.TUESDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val wednesdayUpdate = SingleBusinessHourRequest.builder()
            .date(DayOfWeek.WEDNESDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val thursdayUpdate = SingleBusinessHourRequest.builder()
            .date(DayOfWeek.THURSDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val fridayUpdate = SingleBusinessHourRequest.builder()
            .date(DayOfWeek.FRIDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val saturdayUpdate = SingleBusinessHourRequest.builder()
            .date(DayOfWeek.SATURDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()
        val sundayUpdate = SingleBusinessHourRequest.builder()
            .date(DayOfWeek.SUNDAY)
            .openAt(LocalTime.of(9, 0))
            .closeAt(LocalTime.of(18, 0))
            .build()

        val businessHoursUpdate = listOf(
            mondayUpdate,
            tuesdayUpdate,
            wednesdayUpdate,
            thursdayUpdate,
            fridayUpdate,
            saturdayUpdate,
            sundayUpdate
        )

        val classificationUpdate = Classification.builder()
            .id(3L)
            .type(ClassificationType.CLASSIFICATION)
            .name("대분류 수정")
            .latitude(33.33)
            .longitude(33.33)
            .build()

        val subClassificationUpdate = Classification.builder()
            .id(4L)
            .type(ClassificationType.SUB_CLASSIFICATION)
            .name("소분류 수정")
            .build()

        val request = UpdateStoreRequest.builder()
            .name("협업 지점명 수정")
            .category("카테고리 수정")
            .classificationId(3L)
            .subClassificationId(4L)
            .address("주소 수정")
            .addressDetail("상세 주소 수정")
            .umbrellaLocation("우산 위치 수정")
            .businessHour("근무 시간 수정")
            .contactNumber("연락처 수정")
            .instagramId("인스타그램 주소 수정")
            .latitude(44.44)
            .longitude(44.44)
            .content("내용 수정")
            .build()

        given(storeDetailRepository.findByStoreMetaIdUsingFetchJoin(storeId))
            .willReturn(Optional.of(storeDetail))
        given(classificationService.findClassificationById(request.classificationId!!))
            .willReturn(classificationUpdate)
        given(classificationService.findSubClassificationById(request.subClassificationId!!))
            .willReturn(subClassificationUpdate)
        given(storeMetaService.findStoreMetaById(storeId))
            .willReturn(storeMeta)

        // when
        storeDetailService.updateStore(storeId, request)

        // then
        val foundStoreMeta = storeMetaService.findStoreMetaById(storeId)
        val foundStoreDetail = storeDetailService.findStoreDetailByStoreMetaId(storeId)

        assertAll(
            {
                assertThat(foundStoreMeta)
                    .hasFieldOrPropertyWithValue("classification", classificationUpdate)
                    .hasFieldOrPropertyWithValue("subClassification", subClassificationUpdate)
                    .hasFieldOrPropertyWithValue("category", request.category)
                    .hasFieldOrPropertyWithValue("latitude", request.latitude)
                    .hasFieldOrPropertyWithValue("longitude", request.longitude)
                    .hasFieldOrPropertyWithValue("category", request.category)
            },
            {
                assertThat(foundStoreDetail)
                    .hasFieldOrPropertyWithValue("umbrellaLocation", request.umbrellaLocation)
                    .hasFieldOrPropertyWithValue("workingHour", request.businessHour)
                    .hasFieldOrPropertyWithValue("instaUrl", request.instagramId)
                    .hasFieldOrPropertyWithValue("contactInfo", request.contactNumber)
                    .hasFieldOrPropertyWithValue("address", request.address)
                    .hasFieldOrPropertyWithValue("addressDetail", request.addressDetail)
                    .hasFieldOrPropertyWithValue("content", request.content)
            }
        )
    }

    @Test
    @DisplayName("사용자는 협업지점 소개 페이지를 조회할 수 있다.")
    fun findStoreIntroductionTest() {
        // given
        val storeMeta = StoreMeta(
            id = 3L,
            name = "스타벅스",
            deleted = false,
            latitude = 37.503716,
            longitude = 127.053718,
            activated = true,
            category = "카페, 디저트",
            subClassification = Classification.builder()
                .id(1L)
                .name("카페, 디저트")
                .type(ClassificationType.SUB_CLASSIFICATION)
                .build()
        )

        val storeDetail = StoreDetail(
            id = 2L,
            storeMeta = storeMeta,
            content = "모티브 카페 소개",
            address = "모티브로 32길",
            contactInfo = "010-5252-8282",
            instaUrl = "모티브 인서타",
            workingHour = "매일 7시 ~ 12시",
            umbrellaLocation = "문 앞",
            storeImages = listOf(
                StoreImage(id = 1L, imageUrl = "가게 썸네일")
            )
        )

        val storeIntroductionsResponseByClassification =
            StoreIntroductionsResponseByClassification.builder()
                .subClassificationId(1)
                .stores(
                    listOf(
                        SingleStoreIntroductionResponse.of(
                            3L,
                            "가게 썸네일",
                            "스타벅스",
                            "카페, 디저트"
                        )
                    )
                )
                .build()

        val expected = AllStoreIntroductionResponse.builder()
            .storesByClassification(listOf(storeIntroductionsResponseByClassification))
            .build()

        given(storeDetailRepository.findAllStores())
            .willReturn(listOf(storeDetail))

        // when
        val response = storeDetailService.findAllStoreIntroductions()

        // then
        assertAll(
            {
                assertThat(response)
                    .usingRecursiveComparison()
                    .isEqualTo(expected)
            }
        )
    }
}
