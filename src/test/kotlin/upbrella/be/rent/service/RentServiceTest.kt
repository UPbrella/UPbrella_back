package upbrella.be.rent.service

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
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import upbrella.be.config.FixtureBuilderFactory
import upbrella.be.config.FixtureFactory
import upbrella.be.rent.repository.RentRepository
import upbrella.be.rent.dto.request.HistoryFilterRequest
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest
import upbrella.be.rent.dto.response.RentalHistoriesPageResponse
import upbrella.be.rent.dto.response.RentalHistoryResponse
import upbrella.be.rent.entity.ConditionReport
import upbrella.be.rent.entity.History
import upbrella.be.rent.exception.NonExistingHistoryException
import upbrella.be.rent.exception.NotAvailableUmbrellaException
import upbrella.be.rent.exception.NotRefundedException
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.service.StoreMetaService
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.umbrella.exception.NonExistingBorrowedHistoryException
import upbrella.be.umbrella.service.UmbrellaService
import upbrella.be.user.entity.User
import upbrella.be.user.exception.BlackListUserException
import upbrella.be.user.exception.NonExistingMemberException
import upbrella.be.user.service.UserService
import upbrella.be.util.AesEncryptor
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors

@ExtendWith(MockitoExtension::class)
class RentServiceTest {

    @Mock
    private lateinit var umbrellaService: UmbrellaService

    @Mock
    private lateinit var storeMetaService: StoreMetaService

    @Mock
    private lateinit var rentRepository: RentRepository

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var aesEncryptor: AesEncryptor

    @Mock
    private lateinit var conditionReportService: ConditionReportService

    @InjectMocks
    private lateinit var rentService: RentService

    private lateinit var rentUmbrellaByUserRequest: RentUmbrellaByUserRequest
    private lateinit var foundStoreMeta: StoreMeta
    private lateinit var foundUmbrella: Umbrella
    private lateinit var userToRent: User
    private lateinit var history: History
    private lateinit var filter: HistoryFilterRequest
    private lateinit var conditionReport: ConditionReport

    @BeforeEach
    fun setUp() {
        rentUmbrellaByUserRequest = RentUmbrellaByUserRequest.builder()
            .region("신촌")
            .storeId(25L)
            .umbrellaId(99L)
            .conditionReport("상태 양호")
            .build()

        foundStoreMeta = StoreMeta(
            id = 25L,
            name = "motive study cafe",
            deleted = false,
            category = "category",
            activated = false
        )

        foundUmbrella = Umbrella(
            id = 99L,
            uuid = 99L,
            deleted = false,
            storeMeta = foundStoreMeta,
            rentable = true,
            createdAt = LocalDateTime.now(),
            etc = "etc",
            missed = false,
        )

        userToRent = User(
            0L,
            "테스터",
            "010-1234-5678",
            "email",
            false,
            null,
            null,
            11L
        )

        history = History(
            id = 33L,
            rentedAt = LocalDateTime.of(1000, 12, 3, 4, 24),
            returnedAt = LocalDateTime.of(1000, 12, 3, 4, 25),
            refundedAt = LocalDateTime.of(1000, 12, 3, 4, 26),
            refundedBy = userToRent,
            returnStoreMeta = foundStoreMeta,
            umbrella = foundUmbrella,
            user = userToRent,
            rentStoreMeta = foundStoreMeta,
        )

        conditionReport = ConditionReport(
            id = 1L,
            content = "상태 양호",
            history = history,
            etc = "etc",
        )
    }

    @Nested
    @DisplayName("지역 분류, 협력 지점 고유번호, 우산 고유번호, 선택적으로 상태 신고 내역을 입력받아")
    inner class AddRentTest {

        @Test
        @DisplayName("대여 이력을 정상적으로 추가할 수 있다.")
        fun success() {
            // given
            given(storeMetaService.findStoreMetaById(25L)).willReturn(foundStoreMeta)
            given(umbrellaService.findUmbrellaById(99L)).willReturn(foundUmbrella)
            given(rentRepository.save(any(History::class.java))).willReturn(history)
            doNothing().`when`(conditionReportService)
                .saveConditionReport(any(ConditionReport::class.java))

            // when
            rentService.addRental(rentUmbrellaByUserRequest, userToRent)

            // then
            assertAll(
                {
                    then(umbrellaService).should(times(1))
                        .findUmbrellaById(99L)
                },
                {
                    then(storeMetaService).should(times(1))
                        .findStoreMetaById(25L)
                },
                {
                    then(rentRepository).should(times(1))
                        .save(any(History::class.java))
                }
            )
        }

        @Test
        @DisplayName("협업 지점 고유 번호가 존재하지 않으면 예외 발생")
        fun isNotExistingStore() {
            // given
            given(umbrellaService.findUmbrellaById(99L)).willReturn(foundUmbrella)
            given(storeMetaService.findStoreMetaById(25L)).willThrow(IllegalArgumentException::class.java)

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        rentService.addRental(rentUmbrellaByUserRequest, userToRent)
                    }.isInstanceOf(IllegalArgumentException::class.java)
                },
                {
                    then(umbrellaService).should(times(1)).findUmbrellaById(99L)
                },
                {
                    then(storeMetaService).should(times(1)).findStoreMetaById(25L)
                },
                {
                    then(rentRepository).should(times(1))
                        .findByUserIdAndReturnedAtIsNull(userToRent.id!!)
                }
            )
        }

        @Test
        @DisplayName("우산 고유번호가 존재하지 않으면 예외 발생")
        fun isNotExistingUmbrella() {
            // given
            given(umbrellaService.findUmbrellaById(99L)).willThrow(IllegalArgumentException::class.java)

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        rentService.addRental(rentUmbrellaByUserRequest, userToRent)
                    }.isInstanceOf(IllegalArgumentException::class.java)
                },
                {
                    then(umbrellaService).should(times(1)).findUmbrellaById(99L)
                }
            )
        }
    }

    @Nested
    @DisplayName("사용자는 환급 여부 필터가 가능한 대여/반납 현황을 조회할 수 있다.")
    inner class FindAllHistories {

        private val expectedRentalHistoryResponses = mutableListOf<RentalHistoryResponse>()
        private val generatedHistories =
            mutableListOf<upbrella.be.rent.dto.response.HistoryInfoDto>()

        @Test
        @DisplayName("조건이 없으면 전체 대여/반납 현황을 조회할 수 있다.")
        fun success() {
            // given
            filter = HistoryFilterRequest.builder().build()
            val pageable: Pageable = PageRequest.of(0, 5)

            for (i in 0 until 5) {
                generatedHistories.add(
                    upbrella.be.rent.dto.response.HistoryInfoDto.builder()
                        .id(i.toLong())
                        .name("테스터")
                        .phoneNumber("010-1234-5678")
                        .rentStoreName("motive study cafe")
                        .rentAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                        .umbrellaUuid(99L)
                        .returnStoreName("motive study cafe")
                        .returnAt(LocalDateTime.of(1000, 12, 3, 4, 25))
                        .paidAt(LocalDateTime.of(1000, 12, 3, 4, 26))
                        .bank("국민은행")
                        .accountNumber("1234567890")
                        .etc("etc")
                        .refundedAt(LocalDateTime.of(1000, 12, 3, 4, 27))
                        .build()
                )
            }

            expectedRentalHistoryResponses.addAll(
                generatedHistories.stream()
                    .map(FixtureFactory::buildRentalHistoryResponseWithHistory)
                    .collect(Collectors.toList())
            )

            val historyResponse = RentalHistoriesPageResponse.builder()
                .rentalHistoryResponsePage(expectedRentalHistoryResponses)
                .countOfAllHistories(5L)
                .countOfAllPages(1L)
                .build()

            given(rentRepository.findHistoryInfos(filter, pageable)).willReturn(generatedHistories)
            given(rentRepository.countAll(filter, pageable)).willReturn(5L)

            // when
            val allHistories = rentService.findAllHistories(filter, pageable)

            // then
            assertAll(
                {
                    assertThat(allHistories)
                        .usingRecursiveComparison()
                        .isEqualTo(historyResponse)
                },
                {
                    assertThat(allHistories.rentalHistoryResponsePage.size)
                        .isEqualTo(historyResponse.rentalHistoryResponsePage.size)
                },
                {
                    then(rentRepository).should(times(1)).countAll(filter, pageable)
                },
                {
                    then(rentRepository).should(times(1)).findHistoryInfos(filter, pageable)
                }
            )
        }
    }

    @Nested
    @DisplayName("사용자의 고유 번호를 입력 받아")
    inner class FindUserJavaHistoryTest {

        @Test
        @DisplayName("해당 사용자의 대여 목록을 조회할 수 있다.")
        fun success() {
            // given
            val loginedUserId = 7L

            val historyResponse = upbrella.be.user.dto.response.AllHistoryResponse.builder()
                .histories(
                    listOf(
                        upbrella.be.user.dto.response.SingleHistoryResponse.builder()
                            .umbrellaUuid(99L)
                            .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                            .returnAt(LocalDateTime.of(1000, 12, 3, 4, 25))
                            .rentedStore("motive study cafe")
                            .isRefunded(true)
                            .isReturned(true)
                            .build()
                    )
                ).build()

            given(rentRepository.findAllByUserId(loginedUserId)).willReturn(listOf(history))

            // when
            val userHistory = rentService.findAllHistoriesByUser(loginedUserId)

            // then
            assertAll(
                {
                    assertThat(userHistory)
                        .usingRecursiveComparison()
                        .isEqualTo(historyResponse)
                },
                {
                    assertThat(userHistory.histories.size).isEqualTo(1)
                },
                {
                    then(rentRepository).should(times(1)).findAllByUserId(7L)
                }
            )
        }

        @Test
        @DisplayName("대여 내역이 없으면 빈 목록을 반환한다.")
        fun nonExistingHistory() {
            // given
            val loginedUserId = 7L
            given(rentRepository.findAllByUserId(loginedUserId)).willReturn(listOf())

            // when
            val userHistory = rentService.findAllHistoriesByUser(loginedUserId)

            // then
            assertAll(
                {
                    assertThat(userHistory.histories.size).isEqualTo(0)
                },
                {
                    then(rentRepository).should(times(1)).findAllByUserId(7L)
                }
            )
        }
    }

    @Nested
    @DisplayName("사용자의 고유 번호와 대여 내역의 고유번호를 입력 받아")
    inner class CheckRefundTest {

        @Test
        @DisplayName("해당 대여 내역을 환급 완료 처리할 수 있다.")
        fun success() {
            // given
            val historyForRefund = History(
                id = 33L,
                rentedAt = LocalDateTime.of(1000, 12, 3, 4, 24),
                umbrella = foundUmbrella,
                user = userToRent,
                rentStoreMeta = foundStoreMeta,
            )

            val loginedUserId = 7L
            given(userService.findUserById(loginedUserId)).willReturn(userToRent)
            given(rentRepository.findById(33L)).willReturn(Optional.of(historyForRefund))

            // when
            rentService.checkRefund(33L, loginedUserId)

            // then
            assertAll(
                {
                    assertThat(historyForRefund.refundedBy).isEqualTo(userToRent)
                },
                {
                    assertThat(historyForRefund.refundedAt).isBeforeOrEqualTo(LocalDateTime.now())
                },
                {
                    then(userService).should(times(1)).findUserById(loginedUserId)
                },
                {
                    then(rentRepository).should(times(1)).findById(33L)
                },
                {
                    then(rentRepository).should(times(1)).save(historyForRefund)
                }
            )
        }

        @Test
        @DisplayName("존재하지 않는 사용자면 예외 발생")
        fun nonExistingUser() {
            // given
            val loginedUserId = 7L
            given(userService.findUserById(loginedUserId)).willThrow(NonExistingMemberException::class.java)

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        rentService.checkRefund(33L, loginedUserId)
                    }.isInstanceOf(NonExistingMemberException::class.java)
                },
                {
                    then(userService).should(times(1)).findUserById(loginedUserId)
                },
                {
                    then(rentRepository).shouldHaveNoInteractions()
                }
            )
        }

        @Test
        @DisplayName("존재하지 않는 대여 내역이면 예외 발생")
        fun nonExistingHistory() {
            // given
            val loginedUserId = 7L
            given(userService.findUserById(loginedUserId)).willReturn(userToRent)
            given(rentRepository.findById(33L)).willThrow(NonExistingHistoryException::class.java)

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        rentService.checkRefund(33L, loginedUserId)
                    }.isInstanceOf(NonExistingHistoryException::class.java)
                },
                {
                    then(userService).should(times(1)).findUserById(loginedUserId)
                },
                {
                    then(rentRepository).should(times(1)).findById(33L)
                },
                {
                    then(rentRepository).shouldHaveNoMoreInteractions()
                }
            )
        }
    }

    @Nested
    @DisplayName("사용자의 고유 번호와 대여 내역의 고유번호를 입력 받아")
    inner class CheckPaymentTest {

        @Test
        @DisplayName("해당 대여 내역을 지불 완료 처리할 수 있다.")
        fun success() {
            // given
            val loginedUserId = 7L
            given(userService.findUserById(loginedUserId)).willReturn(userToRent)
            given(rentRepository.findById(33L)).willReturn(Optional.of(history))
            given(rentRepository.save(history)).willReturn(history)

            // when
            rentService.checkPayment(33L, loginedUserId)

            // then
            assertAll(
                {
                    assertThat(history.paidBy).isEqualTo(userToRent)
                },
                {
                    assertThat(history.paidAt).isBeforeOrEqualTo(LocalDateTime.now())
                },
                {
                    then(userService).should(times(1)).findUserById(loginedUserId)
                },
                {
                    then(rentRepository).should(times(1)).findById(33L)
                },
                {
                    then(rentRepository).should(times(1)).save(history)
                }
            )
        }

        @Test
        @DisplayName("존재하지 않는 사용자면 예외 발생")
        fun nonExistingUser() {
            // given
            val loginedUserId = 7L
            given(userService.findUserById(loginedUserId)).willThrow(NonExistingMemberException::class.java)

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        rentService.checkPayment(33L, loginedUserId)
                    }.isInstanceOf(NonExistingMemberException::class.java)
                },
                {
                    then(userService).should(times(1)).findUserById(loginedUserId)
                },
                {
                    then(rentRepository).shouldHaveNoInteractions()
                }
            )
        }

        @Test
        @DisplayName("존재하지 않는 대여 내역이면 예외 발생")
        fun nonExistingHistory() {
            // given
            val loginedUserId = 7L
            given(userService.findUserById(loginedUserId)).willReturn(userToRent)
            given(rentRepository.findById(33L)).willThrow(NonExistingHistoryException::class.java)

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        rentService.checkPayment(33L, loginedUserId)
                    }.isInstanceOf(NonExistingHistoryException::class.java)
                },
                {
                    then(userService).should(times(1)).findUserById(loginedUserId)
                },
                {
                    then(rentRepository).should(times(1)).findById(33L)
                },
                {
                    then(rentRepository).shouldHaveNoMoreInteractions()
                }
            )
        }
    }

    @Nested
    @DisplayName("로그인한 사용자의 정보를 입력받아")
    inner class FindByRentJavaHistoryByUserTest {

        @Test
        @DisplayName("해당 사용자의 대여 내역을 조회할 수 있다.")
        fun success() {
            // given
            val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()
            val history = FixtureBuilderFactory.builderHistory(aesEncryptor).sample()
            given(rentRepository.findByUserIdAndReturnedAtIsNull(sessionUser.id))
                .willReturn(Optional.of(history))

            // when
            val rentalHistoryByUser = rentService.findRentalHistoryByUser(sessionUser)

            // then
            assertAll(
                {
                    assertThat(rentalHistoryByUser).isEqualTo(history)
                },
                {
                    then(rentRepository).should(times(1))
                        .findByUserIdAndReturnedAtIsNull(sessionUser.id)
                }
            )
        }

        @Test
        @DisplayName("빌린 우산이 없으면 예외를 반환")
        fun nonExistingBorrowedUmbrella() {
            // given
            val sessionUser = FixtureBuilderFactory.builderSessionUser().sample()
            given(rentRepository.findByUserIdAndReturnedAtIsNull(sessionUser.id))
                .willThrow(NonExistingBorrowedHistoryException::class.java)

            // when & then
            assertAll(
                {
                    assertThatThrownBy {
                        rentService.findRentalHistoryByUser(sessionUser)
                    }.isInstanceOf(NonExistingBorrowedHistoryException::class.java)
                },
                {
                    then(rentRepository).should(times(1))
                        .findByUserIdAndReturnedAtIsNull(sessionUser.id)
                }
            )
        }
    }

    @Test
    @DisplayName("대여 기록의 계좌 삭제 성공")
    fun deleteRentAccount() {
        // given
        val history = FixtureBuilderFactory.builderHistory(aesEncryptor).sample()

        // when
        history.deleteBankAccount()

        // then
        assertAll(
            {
                assertThat(history.bank).isNull()
            },
            {
                assertThat(history.accountNumber).isNull()
            }
        )
    }

    @Test
    @DisplayName("반납되지 않은 대여 기록 계좌 삭제 실패")
    fun deleteRentAccountThrowTest() {
        // given
        val history = FixtureBuilderFactory.builderHistory(aesEncryptor)
            .set("refundedAt", null)
            .sample()

        // when & then
        assertThatThrownBy {
            history.deleteBankAccount()
        }.isInstanceOf(NotRefundedException::class.java)
    }

    @Test
    @DisplayName("우산이 대여중이면 예외 발생")
    fun notAvailableUmbrellaTest() {
        // given
        val umbrella = Umbrella(
            id = 1L,
            uuid = 99L,
            deleted = false,
            storeMeta = foundStoreMeta,
            rentable = false,
            createdAt = LocalDateTime.now(),
            etc = "etc",
            missed = false,
        )

        given(rentRepository.findByUserIdAndReturnedAtIsNull(userToRent.id!!))
            .willReturn(Optional.empty())
        given(umbrellaService.findUmbrellaById(99L))
            .willReturn(umbrella)

        // when & then
        assertThatThrownBy {
            rentService.addRental(rentUmbrellaByUserRequest, userToRent)
        }.isInstanceOf(NotAvailableUmbrellaException::class.java)
    }

    @Test
    @DisplayName("블랙리스트 유저가 우산을 대여할 경우 예외 발생")
    fun testBlacklistedUserRent() {
        // given
        val user = FixtureBuilderFactory.builderUser(aesEncryptor).sample()
        val request = RentUmbrellaByUserRequest.builder().build()

        doThrow(BlackListUserException::class.java).`when`(userService).checkBlackList(user.id!!)

        // when & then
        assertThatThrownBy {
            rentService.addRental(request, user)
        }.isInstanceOf(BlackListUserException::class.java)
    }
}
