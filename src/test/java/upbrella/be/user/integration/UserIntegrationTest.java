package upbrella.be.user.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.config.FixtureBuilderFactory;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.service.RentService;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.ClassificationType;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.user.controller.UserController;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.request.LoginCodeRequest;
import upbrella.be.user.dto.request.UpdateBankAccountRequest;
import upbrella.be.user.dto.token.KakaoOauthInfo;
import upbrella.be.user.entity.BlackList;
import upbrella.be.user.entity.User;
import upbrella.be.user.repository.BlackListRepository;
import upbrella.be.user.service.OauthLoginService;
import upbrella.be.user.service.UserService;
import upbrella.be.util.AesEncryptor;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserIntegrationTest extends RestDocsSupport {

    @Autowired
    private OauthLoginService oauthLoginService;
    @Autowired
    private UserService userService;
    @Autowired
    private KakaoOauthInfo kakaoOauthInfo;
    @Autowired
    private RentService rentService;
    @Autowired
    private AesEncryptor aesEncryptor;
    @Autowired
    private EntityManager em;
    @Autowired
    private BlackListRepository blackListRepository;
    private MockHttpSession mockHttpSession = new MockHttpSession();
    private User user;

    @Override
    protected Object initController() {
        return new UserController(oauthLoginService, userService, kakaoOauthInfo, rentService);
    }
    @Nested
    class contextJoined {

        @BeforeEach
        void setUp() {
            Classification classification = FixtureBuilderFactory.builderClassification()
                    .set("type", ClassificationType.CLASSIFICATION)
                    .set("id", null).sample();

            Classification subClassification = FixtureBuilderFactory.builderClassification()
                    .set("type", ClassificationType.SUB_CLASSIFICATION)
                    .set("id", null).sample();

            em.persist(classification);
            em.persist(subClassification);
            em.flush();

            user = FixtureBuilderFactory.builderUser(aesEncryptor)
                    .set("id", null)
                    .set("socialId", 1L)
                    .sample();

            em.persist(user);
            em.flush();

            StoreMeta storeMeta = FixtureBuilderFactory.builderStoreMeta()
                    .set("id", null)
                    .set("classification", classification)
                    .set("subClassification", subClassification)
                    .set("businessHours", null)
                    .sample();

            em.persist(storeMeta);
            em.flush();

            Umbrella umbrella = FixtureBuilderFactory.builderUmbrella()
                    .set("id", null)
                    .set("storeMeta", storeMeta)
                    .sample();

            em.persist(umbrella);
            em.flush();

            History history = FixtureBuilderFactory.builderHistory(aesEncryptor)
                    .set("id", null)
                    .set("umbrella", umbrella)
                    .set("user", user)
                    .set("returnedAt", null)
                    .set("rentStoreMeta", storeMeta)
                    .set("returnStoreMeta", null)
                    .set("paidBy", user)
                    .set("refundedBy", null)
                    .sample();

            em.persist(history);
            em.flush();

        }


        @Test
        @DisplayName("사용자는 카카오 소셜 로그인을 할 수 있다.")
        void socialLoginTest() throws Exception {

            // given
            LoginCodeRequest code = LoginCodeRequest.builder().code("1kdfjq0243f").build();

            // when
            mockMvc.perform(
                            post("/users/oauth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(code))
                                    .session(mockHttpSession)
                    ).andDo(print())
                    .andExpect(status().isOk());

            assertThat(mockHttpSession.getAttribute("kakaoUser")).isNotNull();
        }


        @Test
        @DisplayName("사용자는 소셜 로그인 상태에서 업브렐라 로그인을 할 수 있다.")
        void upbrellaLoginTest() throws Exception {

            // given
            LoginCodeRequest code = LoginCodeRequest.builder().code("1kdfjq0243f").build();

            // when & then
            mockMvc.perform(
                            post("/users/oauth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(code))
                                    .session(mockHttpSession)
                    ).andDo(print())
                    .andExpect(status().isOk());

            mockMvc.perform(
                            post("/users/login")
                                    .session(mockHttpSession)
                    ).andDo(print())
                    .andExpect(status().isOk());

            assertThat(mockHttpSession.getAttribute("kakaoUser")).isNull();
            assertThat(mockHttpSession.getAttribute("user")).isNotNull();
        }

        @DisplayName("사용자는 로그인된 유저 정보를 조회할 수 있다.")
        @Test
        void findUserInfoTest() throws Exception {

            // given
            mockHttpSession.setAttribute("user", FixtureBuilderFactory.builderSessionUser().set("id", user.getId()).sample());

            // when & then
            mockMvc.perform(
                            get("/users/loggedIn")
                                    .session(mockHttpSession)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").exists())
                    .andExpect(jsonPath("$.data.phoneNumber").exists())
                    .andExpect(jsonPath("$.data.name").exists())
                    .andExpect(jsonPath("$.data.bank").exists())
                    .andExpect(jsonPath("$.data.accountNumber").exists());
        }

        @Test
        @DisplayName("사용자는 유저가 빌린 우산을 조회할 수 있다.")
        void findUmbrellaBorrowedByUserTest() throws Exception {

            // given
            mockHttpSession.setAttribute("user", FixtureBuilderFactory.builderSessionUser().set("id", user.getId()).sample());

            // when & then
            mockMvc.perform(
                            get("/users/loggedIn/umbrella")
                                    .session(mockHttpSession)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.uuid").exists())
                    .andExpect(jsonPath("$.data.elapsedDay").exists());
        }


        @Test
        @DisplayName("사용자는 로그아웃을 할 수 있다.")
        void logoutTest() throws Exception {

            // given
            mockHttpSession.setAttribute("user", FixtureBuilderFactory.builderSessionUser().set("id", 1L).sample());

            // when
            mockMvc.perform(
                            post("/users/logout")
                                    .session(mockHttpSession)
                    ).andDo(print())
                    .andExpect(status().isOk());

            // then
            assertThat(mockHttpSession.isInvalid()).isEqualTo(true);
        }

        @DisplayName("사용자는 회원 정보 목록을 조회할 수 있다.")
        @Test
        void findUsersInfoTest() throws Exception {

            // when & then
            mockMvc.perform(
                            get("/admin/users")
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.users.length()").value(1))
                    .andExpect(jsonPath("$.data.users[0].id").exists())
                    .andExpect(jsonPath("$.data.users[0].name").exists())
                    .andExpect(jsonPath("$.data.users[0].phoneNumber").exists())
                    .andExpect(jsonPath("$.data.users[0].bank").exists())
                    .andExpect(jsonPath("$.data.users[0].accountNumber").exists())
                    .andExpect(jsonPath("$.data.users[0].adminStatus").exists());
        }

        @Test
        @DisplayName("로그인된 사용자는 우산 대여 정보를 조회할 수 있다.")
        void readUserHistoriesTest() throws Exception {

            // given
            mockHttpSession.setAttribute("user", FixtureBuilderFactory.builderSessionUser().set("id", user.getId()).sample());

            // when & then
            mockMvc.perform(
                            get("/users/histories")
                                    .session(mockHttpSession)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.histories.length()").value(1))
                    .andExpect(jsonPath("$.data.histories[0].umbrellaUuid").exists())
                    .andExpect(jsonPath("$.data.histories[0].rentedAt").exists())
                    .andExpect(jsonPath("$.data.histories[0].rentedStore").exists())
                    .andExpect(jsonPath("$.data.histories[0].returned").value(false))
                    .andExpect(jsonPath("$.data.histories[0].returned").exists());
        }

        @Test
        @DisplayName("사용자는 은행 정보를 수정할 수 있다.")
        void updateUserBankAccount() throws Exception {

            // given
            mockHttpSession.setAttribute("user", FixtureBuilderFactory.builderSessionUser().set("id", user.getId()).sample());
            UpdateBankAccountRequest updateBankAccountRequest = FixtureBuilderFactory.builderBankAccount().sample();

            // when
            mockMvc.perform(
                    patch("/users/bankAccount")
                            .session(mockHttpSession)
                            .content(objectMapper.writeValueAsString(updateBankAccountRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            // then
            User foundUser = em.find(User.class, user.getId());
            Assertions.assertAll(
                    () -> assertThat(aesEncryptor.decrypt(foundUser.getBank())).isEqualTo(updateBankAccountRequest.getBank()),
                    () -> assertThat(aesEncryptor.decrypt(foundUser.getAccountNumber())).isEqualTo(updateBankAccountRequest.getAccountNumber())
            );
        }

        @Test
        @DisplayName("사용자가 회원탈퇴를 하면, 삭제된 회원 정보로 변경되고 회원은 탈퇴된다.")
        void deleteUserTest() throws Exception {

            // given
            mockHttpSession.setAttribute("user", FixtureBuilderFactory.builderSessionUser().set("id", user.getId()).sample());

            // when
            mockMvc.perform(
                    delete("/users/loggedIn")
                            .session(mockHttpSession)
            ).andExpect(status().isOk());

            // then
            User foundUser = em.find(User.class, user.getId());
            Assertions.assertAll(
                    () -> assertThat(foundUser.getSocialId()).isEqualTo(0L),
                    () -> assertThat(foundUser.getAccountNumber()).isEqualTo(null),
                    () -> assertThat(foundUser.getBank()).isEqualTo(null),
                    () -> assertThat(foundUser.getPhoneNumber()).isEqualTo("deleted"),
                    () -> assertThat(foundUser.getName()).isEqualTo("탈퇴한 회원")
            );


        }

        @Test
        @DisplayName("관리자가 회원탈퇴 시키면, 블랙리스트에 추가되고 회원탈퇴가 된다.")
        void withdrawUserTest() throws Exception {

            // given
            long userId = user.getId();

            // when
            mockMvc.perform(
                            delete("/admin/users/{userId}", userId)
                                    .session(mockHttpSession)
                    ).andExpect(status().isOk())
                    .andDo(print());

            // then
            assertThat(blackListRepository.findAll().size()).isEqualTo(1);
            User foundUser = em.find(User.class, user.getId());
            Assertions.assertAll(
                    () -> assertThat(foundUser.getSocialId()).isEqualTo(0L),
                    () -> assertThat(foundUser.getAccountNumber()).isEqualTo(null),
                    () -> assertThat(foundUser.getBank()).isEqualTo(null),
                    () -> assertThat(foundUser.getPhoneNumber()).isEqualTo("deleted"),
                    () -> assertThat(foundUser.getName()).isEqualTo("정지된 회원")
            );
        }



        @Test
        @DisplayName("사용자는 자신의 계좌정보를 삭제할 수 있다.")
        void deleteBackAccountTest() throws Exception {

            // given
            mockHttpSession.setAttribute("user", FixtureBuilderFactory.builderSessionUser().set("id", user.getId()).sample());

            // when
            mockMvc.perform(
                            delete("/users/bankAccount")
                                    .session(mockHttpSession)
                    ).andExpect(status().isOk());

            // then
            assertThat(em.find(User.class, user.getId()).getBank()).isNull();
        }

    }



    @Test
    @DisplayName("사용자는 카카오 소셜 로그인 후 회원 가입을 할 수 있다.")
    void joinTest() throws Exception {

        // given
        LoginCodeRequest code = LoginCodeRequest.builder().code("1kdfjq0243f").build();
        mockMvc.perform(
                        post("/users/oauth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(code))
                                .session(mockHttpSession)
                ).andDo(print())
                .andExpect(status().isOk());
        JoinRequest joinRequest = FixtureBuilderFactory.builderJoinRequest().sample();

        // when
        mockMvc.perform(
                        post("/users/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(joinRequest))
                                .session(mockHttpSession)
                ).andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(em.find(User.class, 1L)).isNotNull();
    }

    @Test
    @DisplayName("사용자는 블랙리스트를 조회할 수 있다.")
    void findAllBlackListTest() throws Exception {

        // given
        LocalDateTime now = LocalDateTime.now();
        BlackList blackList = BlackList.builder()
                .blockedAt(now)
                .socialId(123L)
                .build();

        em.persist(blackList);
        em.flush();

        // then
        mockMvc.perform(
                        get("/users/blackList")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.blackList.length()").value(1))
                .andExpect(jsonPath("$.data.blackList[0].id").value(blackList.getId()))
                .andExpect(jsonPath("$.data.blackList[0].blockedAt").exists());
    }

    @Test
    @DisplayName("사용자는 블랙리스트를 삭제할 수 있다.")
    void deleteBlackListTest() throws Exception {

        // given
        LocalDateTime now = LocalDateTime.now();
        BlackList blackList = BlackList.builder()
                .blockedAt(now)
                .socialId(123L)
                .build();

        em.persist(blackList);
        em.flush();

        // when
        mockMvc.perform(
                        delete("/users/blackList/{blackListId}", blackList.getId())
                ).andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(em.find(BlackList.class, blackList.getId())).isNull();
    }


    @Test
    @DisplayName("관리자가 회원의 관리자 상태를 변경할 수 있다.")
    void updateAdminStatusTest() throws Exception {

        // given
        User user = FixtureBuilderFactory.builderUser(aesEncryptor)
                .set("id", null)
                .set("socialId", 1L)
                .set("adminStatus", false)
                .sample();

        em.persist(user);
        em.flush();

        // when
        mockMvc.perform(
                        patch("/admin/users/{userId}", user.getId())
                ).andDo(print())
                .andExpect(status().isOk());

        // then
        assertThat(em.find(User.class, user.getId()).isAdminStatus()).isTrue();
    }
}
