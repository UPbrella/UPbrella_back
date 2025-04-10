package upbrella.be.store.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import upbrella.be.config.FixtureBuilderFactory
import upbrella.be.store.dto.request.CreateStoreRequest
import upbrella.be.store.dto.request.SingleBusinessHourRequest
import upbrella.be.store.dto.response.SingleCurrentLocationStoreResponse
import upbrella.be.store.dto.response.StoreMetaWithUmbrellaCount
import upbrella.be.store.entity.*
import upbrella.be.store.exception.DeletedStoreDetailException
import upbrella.be.store.exception.EssentialImageException
import upbrella.be.store.exception.NonExistingStoreMetaException
import upbrella.be.store.repository.StoreMetaReader
import upbrella.be.store.repository.StoreMetaWriter
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.umbrella.exception.NonExistingUmbrellaException
import upbrella.be.umbrella.repository.UmbrellaRepository
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class StoreMetaServiceTest {

    @Mock
    private lateinit var umbrellaRepository: UmbrellaRepository

    @Mock
    private lateinit var storeMetaReader: StoreMetaReader

    @Mock
    private lateinit var storeMetaWriter: StoreMetaWriter

    @Mock
    private lateinit var storeDetailService: StoreDetailService

    @Mock
    private lateinit var classificationService: ClassificationService

    @Mock
    private lateinit var businessHourService: BusinessHourService

    @InjectMocks
    private lateinit var storeMetaService: StoreMetaService

    @Nested
    @DisplayName("우산의 고유번호를 입력받아")
    inner class FindCurrentStoreIdByJavaUmbrellaTest {

        @Test
        @DisplayName("해당하는 우산이 보관된 협업 지점의 고유번호, 가게 이름 정보를 성공적으로 반환한다.")
        fun success() {
            // given
            val storeMeta = StoreMeta(
                id = 3L,
                name = "스타벅스",
                activated = false,
                deleted = false,
                category = "카테고리",
            )

            val umbrella = Umbrella(
                id = 2L,
                uuid = 45L,
                deleted = false,
                storeMeta = storeMeta,
                rentable = true,
                createdAt = LocalDateTime.now(),
                etc = "etc",
                missed = false,
            )

            given(umbrellaRepository.findByIdAndDeletedIsFalse(2L))
                .willReturn(Optional.of(umbrella))

            // when
            val currentStoreIdByUmbrella = storeMetaService.findCurrentStoreIdByUmbrella(2L)

            // then
            assertAll(
                { assertThat(currentStoreIdByUmbrella.id).isEqualTo(3L) },
                { assertThat(currentStoreIdByUmbrella.name).isEqualTo("스타벅스") },
                {
                    then(umbrellaRepository).should(times(1))
                        .findByIdAndDeletedIsFalse(2L)
                }
            )
        }

        @Test
        @DisplayName("해당하는 우산이 보관된 협업 지점이 삭제된 상태면 예외를 반환한다.")
        fun isAtDeletedStore() {
            // given
            val storeMeta = StoreMeta(
                id = 3L,
                name = "스타벅스",
                activated = false,
                deleted = true,
                category = "카테고리",
            )

            val umbrella = Umbrella(
                id = 2L,
                uuid = 45L,
                deleted = false,
                storeMeta = storeMeta,
                rentable = true,
                createdAt = LocalDateTime.now(),
                etc = "etc",
                missed = false,
            )

            given(umbrellaRepository.findByIdAndDeletedIsFalse(2L))
                .willReturn(Optional.of(umbrella))

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        storeMetaService.findCurrentStoreIdByUmbrella(2L)
                    }
                        .isInstanceOf(DeletedStoreDetailException::class.java)
                },
                {
                    then(umbrellaRepository).should(times(1))
                        .findByIdAndDeletedIsFalse(2L)
                }
            )
        }

        @Test
        @DisplayName("해당 우산이 존재하지 않으면 예외를 반환한다.")
        fun isNonExistingUmbrella() {
            // given
            given(umbrellaRepository.findByIdAndDeletedIsFalse(2L))
                .willReturn(Optional.empty())

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        storeMetaService.findCurrentStoreIdByUmbrella(2L)
                    }
                        .isInstanceOf(NonExistingUmbrellaException::class.java)
                },
                {
                    then(umbrellaRepository).should(times(1))
                        .findByIdAndDeletedIsFalse(2L)
                }
            )
        }
    }

    @Nested
    @DisplayName("대분류 태그 ID를 입력받아")
    inner class FindStoresInCurrentMapTest {

        private val storeMetaList: MutableList<StoreMetaWithUmbrellaCount> = ArrayList()
        private lateinit var expected: SingleCurrentLocationStoreResponse
        private lateinit var businessHours: List<BusinessHour>

        @BeforeEach
        fun setUp() {
            businessHours = listOf(
                BusinessHour(
                    id = 1L,
                    date = DayOfWeek.MONDAY,
                    openAt = LocalTime.NOON,
                    closeAt = LocalTime.of(23, 0)
                ),
                BusinessHour(
                    id = 2L,
                    date = DayOfWeek.TUESDAY,
                    openAt = LocalTime.NOON,
                    closeAt = LocalTime.of(23, 0)
                ),
                BusinessHour(
                    id = 3L,
                    date = DayOfWeek.WEDNESDAY,
                    openAt = LocalTime.NOON,
                    closeAt = LocalTime.of(23, 0)
                ),
                BusinessHour(
                    id = 4L,
                    date = DayOfWeek.THURSDAY,
                    openAt = LocalTime.NOON,
                    closeAt = LocalTime.of(23, 0)
                ),
                BusinessHour(
                    id = 5L,
                    date = DayOfWeek.FRIDAY,
                    openAt = LocalTime.NOON,
                    closeAt = LocalTime.of(23, 0)
                ),
            )

            val storeIn = StoreMeta(
                id = 1L,
                name = "모티브 카페 신촌 지점",
                activated = true,
                deleted = false,
                category = "카테고리",
                businessHours = businessHours,
                latitude = 4.0,
                longitude = 3.0
            )

            val storeOff = StoreMeta(
                id = 1L,
                name = "모티브 카페 공사 중",
                activated = false,
                deleted = false,
                category = "카테고리",
                businessHours = businessHours,
                latitude = 4.0,
                longitude = 3.0
            )

            val storeMetaWithUmbrellaCount =
                StoreMetaWithUmbrellaCount(
                    storeIn,
                    3L
                )
            val storeMetaWithUmbrellaCount2 =
                StoreMetaWithUmbrellaCount(
                    storeOff,
                    3L
                )

            expected = SingleCurrentLocationStoreResponse(
                id = 1L,
                latitude = 4.0,
                longitude = 3.0,
                name = "모티브 카페 신촌 지점",
                openStatus = true,
                rentableUmbrellasCount = 3L
            )

            storeMetaList.add(storeMetaWithUmbrellaCount)
            storeMetaList.add(storeMetaWithUmbrellaCount2)
        }

        @Test
        @DisplayName("해당 대분류의 협업 지점 및 현재 시각을 토대로 영업 여부를 판단해 정보를 반환한다.")
        fun success() {
            // given
            given(storeMetaReader.findAllStoresByClassification(1L))
                .willReturn(storeMetaList)

            // when
            val storesInCurrentMap = storeMetaService.findAllStoresByClassification(
                1L,
                LocalDateTime.of(2023, 8, 4, 13, 0)
            )

            // then
            assertAll(
                {
                    assertThat(storesInCurrentMap.stores.size).isEqualTo(2)
                },
                {
                    assertThat(storesInCurrentMap.stores[0])
                        .usingRecursiveComparison()
                        .isEqualTo(expected)
                }
            )
        }

        @Test
        @DisplayName("내부 공사로 비활성화 상태인 협업 지점은 영업 시간이어도 영업 중으로 표시되지 않는다.")
        fun isNotActiveStore() {
            // given
            given(storeMetaReader.findAllStoresByClassification(1L))
                .willReturn(storeMetaList)

            // when
            val storesInCurrentMap = storeMetaService.findAllStoresByClassification(
                1L,
                LocalDateTime.of(2023, 8, 4, 13, 0)
            )

            // then
            assertAll(
                {
                    assertThat(storesInCurrentMap.stores.size).isEqualTo(2)
                },
                {
                    assertThat(storesInCurrentMap.stores[1].openStatus).isFalse
                }
            )
        }

        @Test
        @DisplayName("영업 시간이 아닌 협업 지점은 영업 중으로 표시되지 않는다.")
        fun isNotOpen() {
            // given
            given(storeMetaReader.findAllStoresByClassification(1L))
                .willReturn(storeMetaList)

            // when
            val storesInCurrentMap = storeMetaService.findAllStoresByClassification(
                1L,
                LocalDateTime.of(2023, 8, 4, 3, 0)
            )

            // then
            assertAll(
                {
                    assertThat(storesInCurrentMap.stores.size).isEqualTo(2)
                },
                {
                    assertThat(storesInCurrentMap.stores[0].openStatus).isFalse
                }
            )
        }

        @Test
        @DisplayName("만족하는 협업 지점이 없으면 빈 리스트를 반환한다.")
        fun empty() {
            // given
            given(storeMetaReader.findAllStoresByClassification(1L))
                .willReturn(listOf())

            // when
            val storesInCurrentMap = storeMetaService.findAllStoresByClassification(
                1L,
                LocalDateTime.of(1995, 7, 18, 13, 0)
            )

            // then
            assertThat(storesInCurrentMap.stores.size).isEqualTo(0)
        }
    }

    @Nested
    @DisplayName("협업지점 생성 위해 협업지점 정보를 입력받아")
    inner class CreateStoreTest {

        private val store = CreateStoreRequest(
            name = "협업 지점명",
            category = "카테고리",
            classificationId = 1L,
            subClassificationId = 2L,
            activateStatus = true,
            address = "주소",
            addressDetail = "상세주소",
            umbrellaLocation = "우산 위치",
            businessHour = "영업 시간",
            contactNumber = "연락처",
            instagramId = "인스타그램 아이디",
            latitude = 33.33,
            longitude = 33.33,
            content = "내용",
            businessHours = listOf(
                SingleBusinessHourRequest(
                    date = DayOfWeek.MONDAY,
                    openAt = LocalTime.of(10, 0),
                    closeAt = LocalTime.of(20, 0)
                ),
                SingleBusinessHourRequest(
                    date = DayOfWeek.TUESDAY,
                    openAt = LocalTime.of(10, 0),
                    closeAt = LocalTime.of(20, 0)
                ),
                SingleBusinessHourRequest(
                    date = DayOfWeek.WEDNESDAY,
                    openAt = LocalTime.of(10, 0),
                    closeAt = LocalTime.of(20, 0)
                ),
                SingleBusinessHourRequest(
                    date = DayOfWeek.THURSDAY,
                    openAt = LocalTime.of(10, 0),
                    closeAt = LocalTime.of(20, 0)
                ),
                SingleBusinessHourRequest(
                    date = DayOfWeek.FRIDAY,
                    openAt = LocalTime.of(10, 0),
                    closeAt = LocalTime.of(20, 0)
                ),
                SingleBusinessHourRequest(
                    date = DayOfWeek.SATURDAY,
                    openAt = LocalTime.of(10, 0),
                    closeAt = LocalTime.of(20, 0)
                ),
                SingleBusinessHourRequest(
                    date = DayOfWeek.SUNDAY,
                    openAt = LocalTime.of(10, 0),
                    closeAt = LocalTime.of(20, 0)
                )
            )
        )

        @Test
        @DisplayName("새로운 협업지점을 생성할 수 있다.")
        fun createNewStoreTest() {
            // given
            val classificationId = 1L
            val subClassificationId = 2L

            val classification = Classification(
                id = classificationId,
                type = ClassificationType.CLASSIFICATION,
                name = "카테고리",
                latitude = 33.33,
                longitude = 33.33
            )

            val subClassification = Classification(
                id = subClassificationId,
                type = ClassificationType.SUB_CLASSIFICATION,
                name = "카테고리",
            )

            val storeMeta = StoreMeta(
                name = store.name,
                activated = store.activateStatus,
                deleted = false,
                classification = classification,
                subClassification = subClassification,
                category = store.category,
                latitude = store.latitude,
                longitude = store.longitude,
            )

            val storeDetail = StoreDetail(
                id = 1L,
                storeMeta = storeMeta,
                storeImages = listOf(
                    StoreImage(
                        id = 1L,
                        imageUrl = "https://image.com"
                    )
                )
            )

            given(classificationService.findClassificationById(classificationId)).willReturn(
                classification
            )
            given(classificationService.findSubClassificationById(subClassificationId)).willReturn(
                subClassification
            )
            given(storeMetaWriter.save(org.mockito.kotlin.any<StoreMeta>())).willReturn(storeMeta)
            doNothing().`when`(storeDetailService)
                .saveStoreDetail(any<StoreDetail>() ?: storeDetail)
            doNothing().`when`(businessHourService)
                .saveAllBusinessHour(any<List<BusinessHour>>() ?: emptyList())

            // when
            storeMetaService.createStore(store)

            // then
            assertAll(
                {
                    verify(classificationService).findClassificationById(classificationId)
                },
                {
                    verify(classificationService).findSubClassificationById(subClassificationId)
                },
                {
                    verify(storeMetaWriter).save(org.mockito.kotlin.any<StoreMeta>())
                }
            )
        }
    }

    @Test
    @DisplayName("협업지점 삭제 테스트")
    fun deleteStoreMetaTest() {
        // given
        val classification = Classification(
            id = 1L,
            type = ClassificationType.CLASSIFICATION,
            name = "카테고리",
            latitude = 33.33,
            longitude = 33.33
        )

        val subClassification = Classification(
            id = 2L,
            type = ClassificationType.SUB_CLASSIFICATION,
            name = "카테고리",
        )

        val businessHour = BusinessHour(
            id = 1L,
            date = DayOfWeek.MONDAY,
            openAt = LocalTime.of(10, 0),
            closeAt = LocalTime.of(20, 0),
        )

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
            businessHours = listOf(businessHour)
        )

        given(storeMetaReader.findById(1L)).willReturn(storeMeta)

        // when
        storeMetaService.deleteStoreMeta(1L)

        // then
        assertAll(
            {
                verify(storeMetaReader, times(1)).findById(1L)
            },
            {
                assertThat(storeMeta.deleted).isTrue
            }
        )
    }

    @Nested
    @DisplayName("사용자는 ")
    inner class FindStoreMeta {

        // given
        val classification = Classification(
            id = 1L,
            type = ClassificationType.CLASSIFICATION,
            name = "카테고리",
            latitude = 33.33,
            longitude = 33.33
        )

        val subClassification = Classification(
            id = 2L,
            type = ClassificationType.SUB_CLASSIFICATION,
            name = "카테고리",
        )

        val businessHour = BusinessHour(
            id = 1L,
            date = DayOfWeek.MONDAY,
            openAt = LocalTime.of(10, 0),
            closeAt = LocalTime.of(20, 0),
        )

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
            businessHours = listOf(businessHour)
        )

        @Test
        @DisplayName("협업지점을 고유 아이디로 조회할 수 있다.")
        fun test() {

            given(storeMetaReader.findById(1L))
                .willReturn(storeMeta)

            // when
            val foundStoreMeta = storeMetaService.findStoreMetaById(1L)

            // then
            assertAll(
                {
                    verify(storeMetaReader, times(1)).findById(1L)
                },
                {
                    assertThat(foundStoreMeta).isEqualTo(storeMeta)
                }
            )
        }

        @Test
        @DisplayName("협업지점이 존재하지 않으면 예외가 발생한다.")
        fun storeMetaNotFoundTest() {
            // given
            given(storeMetaReader.findById(1L)).willReturn(null)

            // when
            val exception = assertThrows<NonExistingStoreMetaException> {
                storeMetaService.findStoreMetaById(1L)
            }

            // then
            assertThat(exception.message).isEqualTo("[ERROR] 존재하지 않는 협업 지점 고유번호입니다.")
        }
    }

    @Test
    @DisplayName("사용자는 협업지점을 활성화 할 수 있다.")
    fun updateStoreStatusTest() {
        // given
        val storeMeta = FixtureBuilderFactory.builderStoreMeta()
            .set("activated", false)
            .sample()

        val storeDetail = StoreDetail(
            id = 1L,
            storeMeta = storeMeta,
            storeImages = listOf(
                StoreImage(
                    id = 1L,
                    imageUrl = "https://image.com"
                )
            )
        )

        given(storeDetailService.findStoreDetailByStoreMetaId(1L)).willReturn(storeDetail)

        // when
        storeMetaService.activateStoreStatus(1L)

        // then
        assertThat(storeMeta.activated).isTrue
    }

    @Test
    @DisplayName("협업지점의 이미지가 없으면 활성화할 수 없다.")
    fun activateStoreStatusErrorTest() {
        // given
        val storeMeta = FixtureBuilderFactory.builderStoreMeta()
            .set("activated", true)
            .sample()

        val storeDetail = StoreDetail(
            id = 1L,
            storeMeta = storeMeta,
            storeImages = listOf()
        )

        given(storeDetailService.findStoreDetailByStoreMetaId(1L))
            .willReturn(storeDetail)

        // when & then
        assertThatThrownBy {
            storeMetaService.activateStoreStatus(1L)
        }
            .isInstanceOf(EssentialImageException::class.java)
            .hasMessage("[ERROR] 가게 이미지가 존재하지 않으면 영업지점을 활성화할 수 없습니다.")
    }

    @Test
    @DisplayName("사용자는 협업지점을 비활성화 할 수 있다.")
    fun inactivateStoreTest() {
        // given
        val storeMeta = FixtureBuilderFactory.builderStoreMeta()
            .set("activated", true)
            .sample()

        val storeDetail = StoreDetail(
            id = 1L,
            storeMeta = storeMeta,
            storeImages = listOf(
                StoreImage(
                    id = 1L,
                    imageUrl = "https://image.com"
                )
            )
        )

        given(storeDetailService.findStoreDetailByStoreMetaId(1L))
            .willReturn(storeDetail)

        // when
        storeMetaService.inactivateStoreStatus(1L)

        // then
        assertThat(storeMeta.activated).isFalse
    }
}
