package upbrella.be.rent.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.RestDocsSupport;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest;
import upbrella.be.rent.service.RentService;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RentControllerTest extends RestDocsSupport {

    private final RentService rentService = mock(RentService.class);

    @Override
    protected Object initController() {
        return new RentController();
    }

    @DisplayName("사용자는 우산 대여 요청을 할 수 있다.")
    @Test
    void rentUmbrella() throws Exception {

        RentUmbrellaByUserRequest request = RentUmbrellaByUserRequest.builder()
                .region("신촌")
                .storeId(1)
                .umbrellaId(1)
                .statusDeclaration("필요하다면 상태 신고를 해주세요.")
                .build();

        mockMvc.perform(
                        post("/rents")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("rent-umbrella-doc",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("region").type(JsonFieldType.STRING)
                                        .description("지역"),
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER)
                                        .description("대여점 아이디"),
                                fieldWithPath("umbrellaId").type(JsonFieldType.NUMBER)
                                        .description("우산 아이디"),
                                fieldWithPath("statusDeclaration").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("상태 신고")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("데이터 값이 없습니다.")
                        )));
    }

    @DisplayName("사용자는 우산 대여 요청을 할 수 있다.")
    @Test
    void returnUmbrella() throws Exception {
        ReturnUmbrellaByUserRequest request = ReturnUmbrellaByUserRequest.builder()
                .umbrellaId(1)
                .storeId(1)
                .improvement("불편하셨다면 개선 사항을 입력해주세요.")
                .build();


        mockMvc.perform(
                        patch("/rents")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("return-umbrella-doc",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("umbrellaId").type(JsonFieldType.NUMBER)
                                        .description("우산 아이디"),
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER)
                                        .description("대여점 아이디"),
                                fieldWithPath("improvement").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("개선 사항")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("데이터 값이 없습니다.")
                        )));
    }
}