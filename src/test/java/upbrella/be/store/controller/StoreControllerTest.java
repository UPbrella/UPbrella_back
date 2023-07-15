package upbrella.be.store.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.RestDocsSupport;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StoreControllerTest extends RestDocsSupport {


    @Override
    protected Object initController() {
        return new StoreController();
    }

    @Test
    @DisplayName("")
    void findStoreByIdTest() throws Exception {
        // given


        // when


        // then
        mockMvc.perform(
                        get("/stores/{storeId}", 1)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-find-by-id-doc",
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
                                        .description("아이디"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("이름"),
                                fieldWithPath("data.businessHours").type(JsonFieldType.STRING)
                                        .description("영업시간"),
                                fieldWithPath("data.contactNumber").type(JsonFieldType.STRING)
                                        .description("연락처"),
                                fieldWithPath("data.address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("data.availableUmbrellaCount").type(JsonFieldType.NUMBER)
                                        .description("사용가능한 우산 개수"),
                                fieldWithPath("data.openStatus").type(JsonFieldType.BOOLEAN)
                                        .description("오픈 여부"),
                                fieldWithPath("data.coordinate").type(JsonFieldType.STRING)
                                        .description("네이버 길찾기를 위한 좌표")
                        )));
    }
}