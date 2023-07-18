package upbrella.be.umbrella.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.umbrella.service.UmbrellaService;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse;

public class UmbrellaControllerTest extends RestDocsSupport {

    private UmbrellaService umbrellaService = mock(UmbrellaService.class);
    @Override
    protected Object initController() {
        return new UmbrellaController(umbrellaService);
    }

    @DisplayName("사용자는 전체 우산 현황을 조회할 수 있다.")
    @Test
    void showAllUmbrellasTest() throws Exception {

        // given
        List<UmbrellaResponse> umbrellaResponseList = List.of(UmbrellaResponse.builder()
                        .id(1)
                        .storeMetaId(2)
                        .uuid(30)
                        .rentable(true)
                        .build());

        BDDMockito.given(umbrellaService.findAllUmbrellas())
                        .willReturn(umbrellaResponseList);
        // when


        mockMvc.perform(
                        get("/umbrellas")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("show-all-umbrellas-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
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
                                fieldWithPath("data.umbrellaResponsePage[]").type(JsonFieldType.ARRAY)
                                        .description("우산 목록"),
                                fieldWithPath("data.umbrellaResponsePage[].id").type(JsonFieldType.NUMBER)
                                        .description("우산 고유 번호"),
                                fieldWithPath("data.umbrellaResponsePage[].storeMetaId").type(JsonFieldType.NUMBER)
                                        .description("보관 지점 번호"),
                                fieldWithPath("data.umbrellaResponsePage[].uuid").type(JsonFieldType.NUMBER)
                                        .description("우산 관리 번호"),
                                fieldWithPath("data.umbrellaResponsePage[].rentable").type(JsonFieldType.BOOLEAN)
                                        .description("대여 가능 상태")
                        )));
        // then
    }

    @DisplayName("사용자는 지점 우산 현황을 조회할 수 있다.")
    @Test
    void showUmbrellasByStoreIdTest() throws Exception {

        // given
        List<UmbrellaResponse> umbrellaResponseList = List.of(UmbrellaResponse.builder()
                .id(1)
                .storeMetaId(2)
                .uuid(30)
                .rentable(true)
                .build());

        BDDMockito.given(umbrellaService.findUmbrellasByStoreId(2))
                .willReturn(umbrellaResponseList);

        // when

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/umbrellas/{storeId}", 2)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("show-umbrellas-by-store-id-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("지점 고유 번호")
                        ),
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
                                fieldWithPath("data.umbrellaResponsePage[]").type(JsonFieldType.ARRAY)
                                        .description("우산 목록"),
                                fieldWithPath("data.umbrellaResponsePage[].id").type(JsonFieldType.NUMBER)
                                        .description("우산 고유 번호"),
                                fieldWithPath("data.umbrellaResponsePage[].storeMetaId").type(JsonFieldType.NUMBER)
                                        .description("보관 지점 번호"),
                                fieldWithPath("data.umbrellaResponsePage[].uuid").type(JsonFieldType.NUMBER)
                                        .description("우산 관리 번호"),
                                fieldWithPath("data.umbrellaResponsePage[].rentable").type(JsonFieldType.BOOLEAN)
                                        .description("대여 가능 상태")
                        )));
        // then
    }

    @DisplayName("사용자는 새로운 우산을 추가할 수 있다.")
    @Test
    void addUmbrellasTest() throws Exception {

        // given
        UmbrellaRequest umbrellaRequest = UmbrellaRequest.builder()
                .uuid(43)
                .storeMetaId(2)
                .rentable(true)
                .build();

        // when
        mockMvc.perform(
                        post("/umbrellas")
                                .content(objectMapper.writeValueAsString(umbrellaRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("add-umbrellas-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("uuid").type(JsonFieldType.NUMBER)
                                        .description("우산 등록 번호"),
                                fieldWithPath("storeMetaId").type(JsonFieldType.NUMBER)
                                        .description("지점 고유 번호"),
                                fieldWithPath("rentable").type(JsonFieldType.BOOLEAN)
                                        .description("대여 가능 여부")),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("데이터 값이 없습니다.")
                        )));
        // then
    }

    @DisplayName("사용자는 우산 정보를 수정할 수 있다.")
    @Test
    void modifyUmbrellaTest() throws Exception {

        // given
        UmbrellaRequest umbrellaRequest = UmbrellaRequest.builder()
                .uuid(45)
                .storeMetaId(4)
                .rentable(false)
                .build();

        // when
        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/umbrellas/{id}", 1)
                                .content(objectMapper.writeValueAsString(umbrellaRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("modify-umbrella-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("uuid").type(JsonFieldType.NUMBER)
                                        .description("우산 등록 번호"),
                                fieldWithPath("storeMetaId").type(JsonFieldType.NUMBER)
                                        .description("지점 고유 번호"),
                                fieldWithPath("rentable").type(JsonFieldType.BOOLEAN)
                                        .description("대여 가능 여부")),
                        pathParameters(
                                parameterWithName("id").description("우산 고유 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("데이터 값이 없습니다.")
                        )));
        // then
    }

    @DisplayName("사용자는 우산 정보를 삭제할 수 있다.")
    @Test
    void deleteUmbrellaTest() throws Exception {

        // when
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/umbrellas/{id}", 1)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete-umbrella-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("우산 고유 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .optional()
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("데이터 값이 없습니다.")
                        )));
        // then
    }
}
