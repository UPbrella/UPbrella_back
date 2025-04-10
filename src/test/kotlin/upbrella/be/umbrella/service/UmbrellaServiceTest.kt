package upbrella.be.umbrella.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import upbrella.be.config.FixtureBuilderFactory
import upbrella.be.config.FixtureFactory
import upbrella.be.rent.service.RentService
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.exception.NonExistingStoreMetaException
import upbrella.be.store.service.StoreMetaService
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest
import upbrella.be.umbrella.dto.response.UmbrellaResponse
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.umbrella.exception.ExistingUmbrellaUuidException
import upbrella.be.umbrella.exception.NonExistingUmbrellaException
import upbrella.be.umbrella.repository.UmbrellaReader
import upbrella.be.umbrella.repository.UmbrellaWriter
import java.util.stream.Collectors

@ExtendWith(MockitoExtension::class)
class UmbrellaServiceTest {

    @Mock
    private lateinit var umbrellaReader: UmbrellaReader

    @Mock
    private lateinit var umbrellaWriter: UmbrellaWriter

    @Mock
    private lateinit var storeMetaService: StoreMetaService

    @Mock
    private lateinit var rentService: RentService

    @InjectMocks
    private lateinit var umbrellaService: UmbrellaService

    @Nested
    @DisplayName("페이지 번호와 페이지 크기를 입력받아")
    inner class FindAllUmbrellasTest {

        private lateinit var storeMeta: StoreMeta
        private lateinit var expectedUmbrellaResponses: List<UmbrellaResponse>
        private val generatedUmbrellas = mutableListOf<Umbrella>()
        private val generatedUmbrellasWithHistory = mutableListOf<UmbrellaWithHistory>()

        @BeforeEach
        fun setUp() {
            storeMeta = FixtureBuilderFactory.builderStoreMeta().sample()

            repeat(5) {
                generatedUmbrellas.add(
                    FixtureBuilderFactory.builderUmbrella()
                        .set("storeMeta", storeMeta)
                        .sample()
                )
                generatedUmbrellasWithHistory.add(
                    FixtureBuilderFactory.builderUmbrellaWithHistory()
                        .set("storeMeta", storeMeta)
                        .sample()
                )
            }

            expectedUmbrellaResponses = generatedUmbrellasWithHistory.stream()
                .map { umbrella ->
                    FixtureFactory.buildUmbrellaResponseWithUmbrellaAndStoreMeta(umbrella, storeMeta)
                }
                .collect(Collectors.toList())
        }

        @Test
        @DisplayName("해당하는 페이지의 우산 목록 정보를 반환한다.")
        fun success() {
            // given
            val pageable: Pageable = PageRequest.of(0, 5)
            given(umbrellaReader.findUmbrellaAndHistoryOrderedByUmbrellaId(pageable))
                .willReturn(generatedUmbrellasWithHistory)

            // when
            val umbrellaResponseList = umbrellaService.findAllUmbrellas(pageable)

            // then
            assertAll(
                {
                    assertThat(umbrellaResponseList.size)
                        .isEqualTo(expectedUmbrellaResponses.size)
                },
                {
                    assertThat(umbrellaResponseList)
                        .usingRecursiveComparison()
                        .isEqualTo(expectedUmbrellaResponses)
                }
            )
        }

        @Test
        @DisplayName("해당하는 우산이 없으면 빈 객체를 반환한다.")
        fun empty() {
            // given
            val pageable: Pageable = PageRequest.of(0, 5)
            given(umbrellaReader.findUmbrellaAndHistoryOrderedByUmbrellaId(pageable))
                .willReturn(listOf())

            // when
            val umbrellaResponseList = umbrellaService.findAllUmbrellas(pageable)

            // then
            assertThat(umbrellaResponseList).isEmpty()
        }
    }

    @Nested
    @DisplayName("협력 지점의 고유번호와 페이지 번호, 페이지 크기를 입력받아")
    inner class FindUmbrellasByStoreIdTest {

        private lateinit var storeMeta: StoreMeta
        private lateinit var expectedUmbrellaResponses: List<UmbrellaResponse>
        private val generatedUmbrellas = mutableListOf<UmbrellaWithHistory>()
        private val generatedUmbrellasWithHistory = mutableListOf<UmbrellaWithHistory>()

        @BeforeEach
        fun setUp() {
            storeMeta = FixtureBuilderFactory.builderStoreMeta().sample()

            repeat(5) {
                generatedUmbrellas.add(
                    FixtureBuilderFactory.builderUmbrellaWithHistory()
                        .set("storeMeta", storeMeta)
                        .sample()
                )
                generatedUmbrellasWithHistory.add(
                    FixtureBuilderFactory.builderUmbrellaWithHistory()
                        .set("storeMeta", storeMeta)
                        .sample()
                )
            }

            expectedUmbrellaResponses = generatedUmbrellasWithHistory.stream()
                .map { umbrella ->
                    FixtureFactory.buildUmbrellaResponseWithUmbrellaAndStoreMeta(umbrella, storeMeta)
                }
                .collect(Collectors.toList())
        }

        @Test
        @DisplayName("해당하는 페이지의 우산 목록 정보를 반환한다.")
        fun success() {
            // given
            val pageable: Pageable = PageRequest.of(0, 5)
            given(umbrellaReader.findUmbrellaAndHistoryOrderedByUmbrellaIdByStoreId(2L, pageable))
                .willReturn(generatedUmbrellasWithHistory)

            // when
            val umbrellaResponseList = umbrellaService.findUmbrellasByStoreId(2L, pageable)

            // then
            assertAll(
                {
                    assertThat(umbrellaResponseList.size)
                        .isEqualTo(expectedUmbrellaResponses.size)
                },
                {
                    assertThat(umbrellaResponseList)
                        .usingRecursiveComparison()
                        .isEqualTo(expectedUmbrellaResponses)
                }
            )
        }

        @Test
        @DisplayName("해당하는 우산이 없으면 빈 객체를 반환한다.")
        fun empty() {
            // given
            val pageable: Pageable = PageRequest.of(0, 5)
            given(umbrellaReader.findUmbrellaAndHistoryOrderedByUmbrellaIdByStoreId(2L, pageable))
                .willReturn(listOf())

            // when
            val umbrellaResponseList = umbrellaService.findUmbrellasByStoreId(2L, pageable)

            // then
            assertThat(umbrellaResponseList).isEmpty()
        }
    }

    @Nested
    @DisplayName("우산의 고유번호, 협력 지점 고유번호, 대여 여부를 입력받아")
    inner class AddJavaUmbrellaTest {

        private lateinit var umbrellaCreateRequest: UmbrellaCreateRequest
        private lateinit var foundStoreMeta: StoreMeta
        private lateinit var umbrella: Umbrella

        @BeforeEach
        fun setUp() {
            umbrellaCreateRequest = FixtureBuilderFactory.builderUmbrellaCreateRequest().sample()
            foundStoreMeta = FixtureFactory.buildStoreMetaWithId(umbrellaCreateRequest.storeMetaId)
            umbrella = FixtureFactory.buildUmbrellaWithUmbrellaRequestAndStoreMeta(umbrellaCreateRequest, foundStoreMeta)
        }

        @Test
        @DisplayName("우산을 정상적으로 추가할 수 있다.")
        fun success() {
            // given
            given(storeMetaService.findStoreMetaById(foundStoreMeta.id!!))
                .willReturn(foundStoreMeta)
            given(umbrellaReader.existsByUuid(umbrellaCreateRequest.uuid))
                .willReturn(false)
            given(umbrellaWriter.save(org.mockito.kotlin.any<Umbrella>()))
                .willReturn(umbrella)

            // when
            umbrellaService.addUmbrella(umbrellaCreateRequest)

            // then
            assertAll(
                {
                    then(umbrellaReader).should(times(1))
                        .existsByUuid(umbrellaCreateRequest.uuid)
                },
                {
                    then(storeMetaService).should(times(1))
                        .findStoreMetaById(foundStoreMeta.id!!)
                },
                {
                    then(umbrellaWriter).should(times(1))
                        .save(org.mockito.kotlin.any<Umbrella>())
                }
            )
        }

        @Test
        @DisplayName("우산 고유번호가 이미 존재하는 경우 예외를 발생시킨다.")
        fun withSameId() {
            // given
            given(storeMetaService.findStoreMetaById(foundStoreMeta.id!!))
                .willReturn(foundStoreMeta)
            given(umbrellaReader.existsByUuid(umbrella.uuid))
                .willReturn(true)

            // when
            assertThatThrownBy { umbrellaService.addUmbrella(umbrellaCreateRequest) }
                .isInstanceOf(ExistingUmbrellaUuidException::class.java)

            // then
            assertAll(
                {
                    then(storeMetaService).should(times(1))
                        .findStoreMetaById(foundStoreMeta.id!!)
                },
                {
                    then(umbrellaReader).should(times(1))
                        .existsByUuid(umbrellaCreateRequest.uuid)
                },
                {
                    then(umbrellaWriter).should(never())
                        .save(org.mockito.kotlin.any<Umbrella>())
                }
            )
        }

        @Test
        @DisplayName("추가하려고 하는 가게 고유번호가 존재하지 않는 경우 예외를 발생시킨다.")
        fun atNonExistingStore() {
            // given
            given(storeMetaService.findStoreMetaById(foundStoreMeta.id!!))
                .willThrow(NonExistingStoreMetaException("[ERROR] 존재하지 않는 협업지점 ID입니다."))

            // when & then
            assertAll(
                {
                    assertThatThrownBy { umbrellaService.addUmbrella(umbrellaCreateRequest) }
                        .isInstanceOf(NonExistingStoreMetaException::class.java)
                },
                {
                    then(storeMetaService).should(times(1))
                        .findStoreMetaById(foundStoreMeta.id!!)
                },
                {
                    then(umbrellaReader).shouldHaveNoInteractions()
                }
            )
        }
    }

    @Nested
    @DisplayName("우산의 고유번호, 협력 지점 고유번호, 대여 여부를 입력받아")
    inner class ModifyJavaUmbrellaTest {

        private lateinit var umbrellaModifyRequest: UmbrellaModifyRequest
        private lateinit var foundStoreMeta: StoreMeta
        private lateinit var umbrella: Umbrella
        private var id: Long = 0

        @BeforeEach
        fun setUp() {
            id = FixtureBuilderFactory.buildLong(1000)
            umbrellaModifyRequest = FixtureBuilderFactory.builderUmbrellaModifyRequest().sample()
            foundStoreMeta = FixtureFactory.buildStoreMetaWithId(umbrellaModifyRequest.storeMetaId)
            umbrella = FixtureBuilderFactory.builderUmbrella()
                .set("id", id)
                .sample()
        }

        @Test
        @DisplayName("우산을 정상적으로 수정한다.")
        fun success() {
            // given
            given(storeMetaService.findStoreMetaById(foundStoreMeta.id!!))
                .willReturn(foundStoreMeta)
            given(umbrellaReader.findById(umbrella.id!!))
                .willReturn(umbrella)
            given(umbrellaReader.existsByUuid(umbrellaModifyRequest.uuid))
                .willReturn(false)

            // when
            umbrellaService.modifyUmbrella(umbrella.id!!, umbrellaModifyRequest)

            // then
            assertAll(
                {
                    then(umbrellaReader).should(times(1))
                        .existsByUuid(umbrellaModifyRequest.uuid)
                },
                {
                    then(umbrellaReader).should(times(1))
                        .findById(id)
                },
                {
                    then(storeMetaService).should(times(1))
                        .findStoreMetaById(foundStoreMeta.id!!)
                }
            )
        }

        @Test
        @DisplayName("수정하려는 우산 고유번호가 존재하지 않는 경우 예외를 발생시킨다.")
        fun withNonExistingId() {
            // given
            given(storeMetaService.findStoreMetaById(foundStoreMeta.id!!))
                .willReturn(foundStoreMeta)
            given(umbrellaReader.findById(id))
                .willReturn(null)

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        umbrellaService.modifyUmbrella(id, umbrellaModifyRequest)
                    }.isInstanceOf(NonExistingUmbrellaException::class.java)
                },
                {
                    // 우산이 존재하지 않으므로 중복 uuid 체크 로직조차 호출되지 않음
                    then(umbrellaReader).should(never())
                        .existsByUuid(umbrellaModifyRequest.uuid)
                },
                {
                    then(umbrellaReader).should(times(1))
                        .findById(id)
                },
                {
                    then(storeMetaService).should(times(1))
                        .findStoreMetaById(foundStoreMeta.id!!)
                },
                {
                    then(umbrellaWriter).should(never())
                        .save(org.mockito.kotlin.any<Umbrella>())
                }
            )
        }

        @Test
        @DisplayName("수정하려는 우산 관리번호가 이미 존재하는 경우 예외를 발생시킨다.")
        fun withAlreadyExistingUuid() {
            // given
            given(storeMetaService.findStoreMetaById(foundStoreMeta.id!!))
                .willReturn(foundStoreMeta)
            given(umbrellaReader.findById(id))
                .willReturn(umbrella)
            given(umbrellaReader.existsByUuid(umbrellaModifyRequest.uuid))
                .willReturn(true)

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        umbrellaService.modifyUmbrella(id, umbrellaModifyRequest)
                    }.isInstanceOf(ExistingUmbrellaUuidException::class.java)
                },
                {
                    then(umbrellaReader).should(times(1))
                        .existsByUuid(umbrellaModifyRequest.uuid)
                },
                {
                    then(umbrellaReader).should(times(1))
                        .findById(id)
                },
                {
                    then(storeMetaService).should(times(1))
                        .findStoreMetaById(foundStoreMeta.id!!)
                },
                {
                    then(umbrellaWriter).should(never())
                        .save(org.mockito.kotlin.any<Umbrella>())
                }
            )
        }

        @Test
        @DisplayName("추가하려고 하는 가게 고유번호가 존재하지 않는 경우 예외를 발생시킨다.")
        fun atNonExistingStore() {
            // given
            given(storeMetaService.findStoreMetaById(foundStoreMeta.id!!))
                .willThrow(NonExistingStoreMetaException("[ERROR] 존재하지 않는 협업 지점 ID입니다."))

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        umbrellaService.modifyUmbrella(id, umbrellaModifyRequest)
                    }.isInstanceOf(NonExistingStoreMetaException::class.java)
                },
                {
                    then(storeMetaService).should(times(1))
                        .findStoreMetaById(foundStoreMeta.id!!)
                },
                {
                    then(umbrellaReader).shouldHaveNoInteractions()
                }
            )
        }
    }

    @Nested
    @DisplayName("우산의 고유번호를 입력받아")
    inner class DeleteJavaUmbrellaTest {

        private var id: Long = 0
        private lateinit var umbrella: Umbrella

        @BeforeEach
        fun setUp() {
            id = FixtureBuilderFactory.buildLong(1000)
            umbrella = FixtureBuilderFactory.builderUmbrella().sample()
        }

        @Test
        @DisplayName("우산을 정상적으로 삭제한다.")
        fun success() {
            // given
            given(umbrellaReader.findById(id))

                .willReturn(umbrella)
            // when
            umbrellaService.deleteUmbrella(id)

            // then
            assertAll(
                {
                    assertThat(umbrella.deleted).isTrue
                },
                {
                    then(umbrellaReader).should(times(1))
                        .findById(id)
                }
            )
        }

        @Test
        @DisplayName("우산이 이미 삭제되었거나 고유번호가 존재하지 않는 경우 예외를 발생시킨다.")
        fun alreadyDeletedOrNonExistingId() {
            // given
            given(umbrellaReader.findById(id))
                .willReturn(null)

            // when & then
            assertAll(
                {
                    assertThatThrownBy { umbrellaService.deleteUmbrella(id) }
                        .isInstanceOf(NonExistingUmbrellaException::class.java)
                },
                {
                    then(umbrellaReader).should(times(1))
                        .findById(id)
                }
            )
        }
    }

    @Nested
    @DisplayName("협업 지점의 고유 번호를 입력받아")
    inner class CountAvailableJavaUmbrellaAtStoreTest {

        @Test
        @DisplayName("해당 협업 지점에서 현재 이용 가능한 우산의 개수를 반환한다.")
        fun success() {
            // given
            val id = FixtureBuilderFactory.buildLong(1000)
            val availableCount = FixtureBuilderFactory.buildInteger(100).toLong()

            given(umbrellaReader.countRentableUmbrellasByStore(id))
                .willReturn(availableCount)

            // when
            val count = umbrellaService.countAvailableUmbrellaAtStore(id)

            // then
            assertAll(
                {
                    assertThat(count).isEqualTo(availableCount)
                },
                {
                    then(umbrellaReader).should(times(1))
                        .countRentableUmbrellasByStore(id)
                }
            )
        }
    }

    @Test
    @DisplayName("전체 우산의 통계를 조회할 수 있다.")
    fun getUmbrellaAllStatisticsTest() {
        // given
        val expected = FixtureBuilderFactory.builderUmbrellaStatisticsResponse().sample()

        given(umbrellaReader.countAllUmbrellas())
            .willReturn(expected.totalUmbrellaCount)
        given(umbrellaReader.countRentableUmbrellas())
            .willReturn(expected.rentableUmbrellaCount)
        given(umbrellaReader.countRentedUmbrellas())
            .willReturn(expected.rentedUmbrellaCount)
        given(umbrellaReader.countMissingUmbrellas())
            .willReturn(expected.missingUmbrellaCount)
        given(rentService.countTotalRent())
            .willReturn(expected.totalRentCount)

        // when
        val umbrellaAllStatistics = umbrellaService.getUmbrellaAllStatistics()

        // then
        assertAll(
            {
                assertThat(umbrellaAllStatistics)
                    .usingRecursiveComparison()
                    .isEqualTo(expected)
            },
            {
                then(umbrellaReader).should(times(1))
                    .countAllUmbrellas()
            },
            {
                then(umbrellaReader).should(times(1))
                    .countRentableUmbrellas()
            },
            {
                then(umbrellaReader).should(times(1))
                    .countRentedUmbrellas()
            },
            {
                then(umbrellaReader).should(times(1))
                    .countMissingUmbrellas()
            },
            {
                then(rentService).should(times(1))
                    .countTotalRent()
            }
        )
    }

    @Nested
    @DisplayName("협업 지점의 고유 번호를 입력받아")
    inner class GetJavaUmbrellaStatisticsByStoreTest {

        @Test
        @DisplayName("지점 우산의 통계를 조회할 수 있다.")
        fun success() {
            // given
            val expected = FixtureBuilderFactory.builderUmbrellaStatisticsResponse().sample()

            val storeId = FixtureBuilderFactory.buildLong(1000)

            given(storeMetaService.existByStoreId(storeId))
                .willReturn(true)
            given(umbrellaReader.countAllUmbrellasByStore(storeId))
                .willReturn(expected.totalUmbrellaCount)
            given(umbrellaReader.countRentableUmbrellasByStore(storeId))
                .willReturn(expected.rentableUmbrellaCount)
            given(umbrellaReader.countRentedUmbrellasByStore(storeId))
                .willReturn(expected.rentedUmbrellaCount)
            given(umbrellaReader.countMissingUmbrellasByStore(storeId))
                .willReturn(expected.missingUmbrellaCount)
            given(rentService.countTotalRentByStoreId(storeId))
                .willReturn(expected.totalRentCount)

            // when
            val umbrellaStatistics = umbrellaService.getUmbrellaStatisticsByStoreId(storeId)

            // then
            assertAll(
                {
                    assertThat(umbrellaStatistics)
                        .usingRecursiveComparison()
                        .isEqualTo(expected)
                },
                {
                    then(umbrellaReader).should(times(1))
                        .countAllUmbrellasByStore(storeId)
                },
                {
                    then(umbrellaReader).should(times(1))
                        .countRentableUmbrellasByStore(storeId)
                },
                {
                    then(umbrellaReader).should(times(1))
                        .countRentedUmbrellasByStore(storeId)
                },
                {
                    then(umbrellaReader).should(times(1))
                        .countMissingUmbrellasByStore(storeId)
                },
                {
                    then(storeMetaService).should(times(1))
                        .existByStoreId(storeId)
                },
                {
                    then(rentService).should(times(1))
                        .countTotalRentByStoreId(storeId)
                }
            )
        }

        @Test
        @DisplayName("협업 지점이 존재하지 않는 경우 예외를 발생시킨다.")
        fun nonExistingStore() {
            // given
            val storeId = FixtureBuilderFactory.buildLong(1000)
            given(storeMetaService.existByStoreId(storeId))
                .willReturn(false)

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        umbrellaService.getUmbrellaStatisticsByStoreId(storeId)
                    }.isInstanceOf(NonExistingStoreMetaException::class.java)
                },
                {
                    then(storeMetaService).should(times(1))
                        .existByStoreId(storeId)
                }
            )
        }
    }
}
