package upbrella.be.user.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import upbrella.be.config.FixtureBuilderFactory
import upbrella.be.config.FixtureFactory
import upbrella.be.docs.utils.RestDocsSupport
import upbrella.be.rent.service.RentService
import upbrella.be.store.entity.ClassificationType
import upbrella.be.user.controller.UserController
import upbrella.be.user.dto.request.KakaoAccount
import upbrella.be.user.dto.request.LoginCodeRequest
import upbrella.be.user.dto.response.KakaoLoginResponse
import upbrella.be.user.dto.token.AppleOauthInfo
import upbrella.be.user.dto.token.KakaoOauthInfo
import upbrella.be.user.dto.token.OauthToken
import upbrella.be.user.entity.BlackList
import upbrella.be.user.entity.User
import upbrella.be.user.repository.BlackListRepository
import upbrella.be.user.repository.UserRepository
import upbrella.be.user.service.BlackListService
import upbrella.be.user.service.OauthLoginService
import upbrella.be.user.service.UserService
import upbrella.be.util.AesEncryptor
import java.time.LocalDateTime
import javax.persistence.EntityManager

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserIntegrationTest : RestDocsSupport() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var oauthLoginService: OauthLoginService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var kakaoOauthInfo: KakaoOauthInfo

    @Autowired
    private lateinit var appleOauthInfo: AppleOauthInfo

    @Autowired
    private lateinit var rentService: RentService

    @Autowired
    private lateinit var aesEncryptor: AesEncryptor

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var blackListService: BlackListService

    @Autowired
    private lateinit var blackListRepository: BlackListRepository

    private var mockHttpSession: MockHttpSession = MockHttpSession()
    private lateinit var user: User

    override fun initController(): Any {
        return UserController(
            oauthLoginService = oauthLoginService,
            userService = userService,
            kakaoOauthInfo = kakaoOauthInfo,
            appleOauthInfo = appleOauthInfo,
            rentService = rentService,
            blackListService = blackListService,
        )
    }

    @Nested
    inner class ContextJoined {

        @BeforeEach
        fun setUp() {
            val classification = FixtureBuilderFactory.builderClassification()
                .set("type", ClassificationType.CLASSIFICATION)
                .set("id", null)
                .sample()

            val subClassification = FixtureBuilderFactory.builderClassification()
                .set("type", ClassificationType.SUB_CLASSIFICATION)
                .set("id", null)
                .sample()

            em.persist(classification)
            em.persist(subClassification)
            em.flush()

            user = FixtureBuilderFactory.builderUser(aesEncryptor)
                .set("id", null)
                .set("socialId", 1L)
                .sample()

            em.persist(user)
            em.flush()

            val storeMeta = FixtureBuilderFactory.builderStoreMeta()
                .set("id", null)
                .set("classification", classification)
                .set("subClassification", subClassification)
                .set("businessHours", null)
                .sample()

            em.persist(storeMeta)
            em.flush()

            val umbrella = FixtureBuilderFactory.builderUmbrella()
                .set("id", null)
                .set("storeMeta", storeMeta)
                .sample()

            em.persist(umbrella)
            em.flush()

            val history = FixtureBuilderFactory.builderHistory(aesEncryptor)
                .set("id", null)
                .set("umbrella", umbrella)
                .set("user", user)
                .set("returnedAt", null)
                .set("rentStoreMeta", storeMeta)
                .set("returnStoreMeta", null)
                .set("paidBy", user)
                .set("refundedBy", null)
                .sample()

            em.persist(history)
            em.flush()
        }

        @Test
        @DisplayName("사용자는 카카오 소셜 로그인을 할 수 있다.")
        fun socialLoginTest() {
            // given
            val code = LoginCodeRequest(code = "1kdfjq0243f")
            given(
                oauthLoginService.getOauthToken(
                    any<String>() ?: "code",
                    eq(kakaoOauthInfo) ?: kakaoOauthInfo
                )
            )
                .willReturn(OauthToken("accessToken", "refreshToken", "bearer", 3600L))
            given(
                oauthLoginService.processKakaoLogin(
                    any<String>() ?: "accessToken",
                    any<String>() ?: "loginUrl"
                )
            )
                .willReturn(FixtureFactory.buildKakaoLoginResponse())
            // when
            mockMvc.perform(
                post("/users/oauth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(code))
                    .session(mockHttpSession)
            )
                .andDo(print())
                .andExpect(status().isOk)

            // then
            assertThat(mockHttpSession.getAttribute("kakaoUser")).isNotNull
        }

        @Test
        @DisplayName("사용자는 소셜 로그인 상태에서 업브렐라 로그인을 할 수 있다.")
        fun upbrellaLoginTest() {
            // given
            val code = LoginCodeRequest(code = "1kdfjq0243f")
            given(
                oauthLoginService.getOauthToken(
                    any<String>() ?: "code",
                    eq(kakaoOauthInfo) ?: kakaoOauthInfo
                )
            )
                .willReturn(OauthToken("accessToken", "refreshToken", "bearer", 3600L))

            mockHttpSession.setAttribute(
                "kakaoUser",
                KakaoLoginResponse(user.socialId, KakaoAccount("email"))
            )

            given(
                oauthLoginService.processKakaoLogin(
                    any<String>() ?: "accessToken",
                    any<String>() ?: "loginUrl"
                )
            )
                .willReturn(KakaoLoginResponse(user.socialId, KakaoAccount("email")))

            mockMvc.perform(
                post("/users/oauth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(code))
                    .session(mockHttpSession)
            )
                .andDo(print())
                .andExpect(status().isOk)

            // when & then
            mockMvc.perform(
                post("/users/login")
                    .session(mockHttpSession)
            )
                .andDo(print())
                .andExpect(status().isOk)

            assertThat(mockHttpSession.getAttribute("kakaoUser")).isNull()
            assertThat(mockHttpSession.getAttribute("user")).isNotNull
        }

        @Test
        @DisplayName("사용자는 로그인된 유저 정보를 조회할 수 있다.")
        fun findUserInfoTest() {
            // given
            mockHttpSession.setAttribute(
                "user",
                FixtureBuilderFactory.builderSessionUser().set("id", user.id).sample()
            )

            // when & then
            mockMvc.perform(
                get("/users/loggedIn")
                    .session(mockHttpSession)
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.phoneNumber").exists())
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.bank").exists())
                .andExpect(jsonPath("$.data.accountNumber").exists())
        }

        @Test
        @DisplayName("사용자는 유저가 빌린 우산을 조회할 수 있다.")
        fun findUmbrellaBorrowedByUserTest() {
            // given
            mockHttpSession.setAttribute(
                "user",
                FixtureBuilderFactory.builderSessionUser().set("id", user.id).sample()
            )

            // when & then
            mockMvc.perform(
                get("/users/loggedIn/umbrella")
                    .session(mockHttpSession)
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.uuid").exists())
                .andExpect(jsonPath("$.data.elapsedDay").exists())
        }

        @Test
        @DisplayName("사용자는 로그아웃을 할 수 있다.")
        fun logoutTest() {
            // given
            mockHttpSession.setAttribute(
                "user",
                FixtureBuilderFactory.builderSessionUser().set("id", 1L).sample()
            )

            // when
            mockMvc.perform(
                post("/users/logout")
                    .session(mockHttpSession)
            )
                .andDo(print())
                .andExpect(status().isOk)

            // then
            assertThat(mockHttpSession.isInvalid).isTrue
        }

        @Test
        @DisplayName("사용자는 회원 정보 목록을 조회할 수 있다.")
        fun findUsersInfoTest() {
            // when & then
            mockMvc.perform(
                get("/admin/users")
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.users.length()").value(1))
                .andExpect(jsonPath("$.data.users[0].id").exists())
                .andExpect(jsonPath("$.data.users[0].name").exists())
                .andExpect(jsonPath("$.data.users[0].phoneNumber").exists())
                .andExpect(jsonPath("$.data.users[0].bank").exists())
                .andExpect(jsonPath("$.data.users[0].accountNumber").exists())
                .andExpect(jsonPath("$.data.users[0].adminStatus").exists())
        }

        @Test
        @DisplayName("로그인된 사용자는 우산 대여 정보를 조회할 수 있다.")
        fun readUserHistoriesTest() {
            // given
            mockHttpSession.setAttribute(
                "user",
                FixtureBuilderFactory.builderSessionUser().set("id", user.id).sample()
            )

            // when & then
            mockMvc.perform(
                get("/users/histories")
                    .session(mockHttpSession)
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.histories.length()").value(1))
                .andExpect(jsonPath("$.data.histories[0].umbrellaUuid").exists())
                .andExpect(jsonPath("$.data.histories[0].rentedAt").exists())
                .andExpect(jsonPath("$.data.histories[0].rentedStore").exists())
                .andExpect(jsonPath("$.data.histories[0].isReturned").exists())
        }

        @Test
        @DisplayName("사용자는 은행 정보를 수정할 수 있다.")
        fun updateUserBankAccount() {
            // given
            mockHttpSession.setAttribute(
                "user",
                FixtureBuilderFactory.builderSessionUser().set("id", user.id).sample()
            )
            val updateBankAccountRequest = FixtureBuilderFactory.builderBankAccount().sample()

            // when
            mockMvc.perform(
                patch("/users/bankAccount")
                    .session(mockHttpSession)
                    .content(objectMapper.writeValueAsString(updateBankAccountRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)

            // then
            val foundUser = em.find(User::class.java, user.id)
            Assertions.assertAll(
                {
                    assertThat(aesEncryptor.decrypt(foundUser.bank))
                        .isEqualTo(updateBankAccountRequest.bank)
                },
                {
                    assertThat(aesEncryptor.decrypt(foundUser.accountNumber))
                        .isEqualTo(updateBankAccountRequest.accountNumber)
                }
            )
        }

        @Test
        @DisplayName("사용자가 회원탈퇴를 하면, 삭제된 회원 정보로 변경되고 회원은 탈퇴된다.")
        fun deleteUserTest() {
            // given
            mockHttpSession.setAttribute(
                "user",
                FixtureBuilderFactory.builderSessionUser().set("id", user.id).sample()
            )

            // when
            mockMvc.perform(
                delete("/users/loggedIn")
                    .session(mockHttpSession)
            )
                .andExpect(status().isOk)

            // then
            val foundUser = em.find(User::class.java, user.id)
            Assertions.assertAll(
                { assertThat(foundUser.socialId).isEqualTo(-(user.id!!)) },
                { assertThat(foundUser.accountNumber).isNull() },
                { assertThat(foundUser.bank).isNull() },
                { assertThat(foundUser.phoneNumber).isEqualTo("deleted") },
                { assertThat(foundUser.email).isEqualTo("deleted") },
                { assertThat(foundUser.name).isEqualTo("탈퇴한 회원") }
            )
        }

        @Test
        @DisplayName("관리자가 회원탈퇴 시키면, 블랙리스트에 추가되고 회원탈퇴가 된다.")
        fun withdrawUserTest() {
            // given
            val userId = user.id

            // when
            mockMvc.perform(
                delete("/admin/users/{userId}", userId)
                    .session(mockHttpSession)
            )
                .andExpect(status().isOk)
                .andDo(print())

            // then
            assertThat(blackListRepository.findAll().size).isEqualTo(1)
            val foundUser = em.find(User::class.java, user.id)
            Assertions.assertAll(
                { assertThat(foundUser.socialId).isEqualTo(-(user.id!!)) },
                { assertThat(foundUser.accountNumber).isNull() },
                { assertThat(foundUser.bank).isNull() },
                { assertThat(foundUser.phoneNumber).isEqualTo("deleted") },
                { assertThat(foundUser.email).isEqualTo("deleted") },
                { assertThat(foundUser.name).isEqualTo("정지된 회원") }
            )
        }

        @Test
        @DisplayName("사용자는 자신의 계좌정보를 삭제할 수 있다.")
        fun deleteBackAccountTest() {
            // given
            mockHttpSession.setAttribute(
                "user",
                FixtureBuilderFactory.builderSessionUser().set("id", user.id).sample()
            )

            // when
            mockMvc.perform(
                delete("/users/bankAccount")
                    .session(mockHttpSession)
            )
                .andExpect(status().isOk)

            // then
            assertThat(em.find(User::class.java, user.id).bank).isNull()
        }
    }

    @Test
    @DisplayName("사용자는 카카오 소셜 로그인 후 회원 가입을 할 수 있다.")
    fun joinTest() {
        // given
        val code = LoginCodeRequest(code = "1kdfjq0243f")
        given(
            oauthLoginService.getOauthToken(
                any<String>() ?: "code",
                eq(kakaoOauthInfo) ?: kakaoOauthInfo
            )
        )
            .willReturn(OauthToken("accessToken", "refreshToken", "bearer", 3600L))

        mockMvc.perform(
            post("/users/oauth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(code))
                .session(mockHttpSession)
        )
            .andDo(print())
            .andExpect(status().isOk)

        val joinRequest = FixtureBuilderFactory.builderJoinRequest().sample()

        mockHttpSession.setAttribute("kakaoUser", FixtureFactory.buildKakaoLoginResponse())
        // when
        mockMvc.perform(
            post("/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinRequest))
                .session(mockHttpSession)
        )
            .andDo(print())
            .andExpect(status().isOk)

        // then
        val user = userRepository.findAll().firstOrNull()
        assertThat(user).isNotNull
    }

    @Test
    @DisplayName("사용자는 블랙리스트를 조회할 수 있다.")
    fun findAllBlackListTest() {
        // given
        val now = LocalDateTime.now()
        val blackList = BlackList.createNewBlackList(123L, now)

        em.persist(blackList)
        em.flush()

        // when & then
        mockMvc.perform(
            get("/users/blackList")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.blackList.length()").value(1))
            .andExpect(jsonPath("$.data.blackList[0].id").value(blackList.id))
            .andExpect(jsonPath("$.data.blackList[0].blockedAt").exists())
    }

    @Test
    @DisplayName("사용자는 블랙리스트를 삭제할 수 있다.")
    fun deleteBlackListTest() {
        // given
        val now = LocalDateTime.now()
        val blackList = BlackList.createNewBlackList(123L, now)

        em.persist(blackList)
        em.flush()

        // when
        mockMvc.perform(
            delete("/users/blackList/{blackListId}", blackList.id)
        )
            .andDo(print())
            .andExpect(status().isOk)

        // then
        assertThat(em.find(BlackList::class.java, blackList.id)).isNull()
    }

    @Test
    @DisplayName("관리자가 회원의 관리자 상태를 변경할 수 있다.")
    fun updateAdminStatusTest() {
        // given
        val user = FixtureBuilderFactory.builderUser(aesEncryptor)
            .set("id", null)
            .set("socialId", 1L)
            .set("adminStatus", false)
            .sample()

        em.persist(user)
        em.flush()

        // when
        mockMvc.perform(
            patch("/admin/users/{userId}", user.id)
        )
            .andDo(print())
            .andExpect(status().isOk)

        // then
        assertThat(em.find(User::class.java, user.id).adminStatus).isTrue
    }
}
