package upbrella.be.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.RestDocsSupport;
import upbrella.be.user.controller.UserController;
import upbrella.be.user.dto.response.UserInfoResponse;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new UserController();
    }

    @DisplayName("테스트")
    @Test
    void test() throws Exception {

        // given
        UserInfoResponse user = UserInfoResponse.builder()
                .name("일반사용자")
                .phoneNumber("010-0000-0000")
                .isAdmin(false)
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
                                        .optional()
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("데이터"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING)
                                        .description("사용자 전화번호"),
                                fieldWithPath("data.admin").type(JsonFieldType.BOOLEAN)
                                        .description("관리자 여부")
                        )));
        // then
    }
}
