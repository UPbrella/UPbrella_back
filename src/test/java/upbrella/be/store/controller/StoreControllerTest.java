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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StoreControllerTest extends RestDocsSupport {


    @Override
    protected Object initController() {
        return new StoreController();
    }

    @Test
    @DisplayName("스토어의 아이디로 스토어를 상세조회할 수 있다.")
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
                        pathParameters(
                                parameterWithName("storeId")
                                        .description("가져올 스토어의 아이디")
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

    @Test
    @DisplayName("우산의 현위치를 조회할 수 있다. ")
    void findCurrentUmbrellaStoreTest() throws Exception {
        // given


        // when


        // then
        mockMvc.perform(
                        get("/stores/location/{umbrellaName}", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-find-current-umbrella-store-doc",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("umbrellaName")
                                        .description("현재 우산의 이름")
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
                                fieldWithPath("data.storeId").type(JsonFieldType.NUMBER)
                                        .description("스토어 아이디"),
                                fieldWithPath("data.storeName").type(JsonFieldType.STRING)
                                        .description("스토어 이름")
                        )));
    }

    @Test
    @DisplayName("관리자 페이지에서 전체 협업지점 목록을 보여줄 수 있다.")
    void findAllStoreTest() throws Exception {
        // given


        // when


        // then
        mockMvc.perform(
                        get("/stores")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-find-all-doc",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data.stores[]").type(JsonFieldType.ARRAY)
                                        .description("데이터"),
                                fieldWithPath("data.stores[].id").type(JsonFieldType.NUMBER)
                                        .description("아이디"),
                                fieldWithPath("data.stores[].name").type(JsonFieldType.STRING)
                                        .description("협업지점명"),
                                fieldWithPath("data.stores[].classification").type(JsonFieldType.STRING)
                                        .description("분류"),
                                fieldWithPath("data.stores[].activateStatus").type(JsonFieldType.BOOLEAN)
                                        .description("활성화 여부"),
                                fieldWithPath("data.stores[].address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("data.stores[].umbrellaLocation").type(JsonFieldType.STRING)
                                        .description("우산 위치"),
                                fieldWithPath("data.stores[].businessHours").type(JsonFieldType.STRING)
                                        .description("영업시간"),
                                fieldWithPath("data.stores[].contactNumber").type(JsonFieldType.STRING)
                                        .description("연락처"),
                                fieldWithPath("data.stores[].instagramId").type(JsonFieldType.STRING)
                                        .description("인스타그램 아이디"),
                                fieldWithPath("data.stores[].coordinate").type(JsonFieldType.STRING)
                                        .description("네이버 길찾기를 위한 좌표"),
                                fieldWithPath("data.stores[].imageUrls[]").type(JsonFieldType.ARRAY)
                                        .description("이미지 URL")
                        )));

    }
}
