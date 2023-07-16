package upbrella.be.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.RestDocsSupport;
import upbrella.be.user.controller.UserController;
import upbrella.be.user.dto.response.UserInfoResponse;
import upbrella.be.user.service.UserService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);

    @Override
    protected Object initController() {
        return new UserController(userService);
    }

    @DisplayName("테스트")
    @Test
    void test() throws Exception {

        // given
        UserInfoResponse user = UserInfoResponse.builder()
                .id(1L)
                .socialId(1L)
                .name("일반사용자")
                .phoneNumber("010-0000-0000")
                .adminStatus(false)
                .build();

        // when
        mockMvc.perform(
                        get("/users")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("find-user-info-doc",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("사용자 식별자"),
                                fieldWithPath("data.socialId").type(JsonFieldType.NUMBER)
                                        .description("사용자 소셜 식별자"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING)
                                        .description("사용자 전화번호"),
                                fieldWithPath("data.adminStatus").type(JsonFieldType.BOOLEAN)
                                        .description("관리자 여부")
                        )));
    }

    @Test
    @DisplayName("사용자는 유저가 빌린 우산을 조회할 수 있다.")
    void findUmbrellaBorrowedByUserTest() throws Exception {
        // given

        given(userService.findUmbrellaBorrowedByUser(anyLong()))
                .willReturn(1);

        // when
        mockMvc.perform(
                        get("/users/umbrella")
                                .content("{\"userId\": 1}")
                                .contentType("application/json")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("find-umbrella-borrowed-by-user-doc",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER)
                                        .description("사용자 식별자")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("데이터"),
                                fieldWithPath("data.name").type(JsonFieldType.NUMBER)
                                        .description("사용자가 빌린 우산 이름")
                        )));
    }
}
