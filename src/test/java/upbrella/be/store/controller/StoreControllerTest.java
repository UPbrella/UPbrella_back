package upbrella.be.store.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.RestDocsSupport;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.request.UpdateStoreRequest;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
    @DisplayName("사용자는 우산의 위도, 경도, 확대 정도를 기반으로 협업지점을 조회할 수 있다.")
    void test() throws Exception {
        // given


        // when


        // then
        mockMvc.perform(
                        get("/stores/location")
                                .param("latitude", "37.5666103")
                                .param("longitude", "126.9783882")
                                .param("zoomLevel", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-find-by-location-doc",
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("latitude")
                                        .description("위도"),
                                parameterWithName("longitude")
                                        .description("경도"),
                                parameterWithName("zoomLevel")
                                        .description("확대 정도")
                        ),
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
                                fieldWithPath("data.stores[].openStatus").type(JsonFieldType.BOOLEAN)
                                        .description("오픈 여부"),
                                fieldWithPath("data.stores[].latitude").type(JsonFieldType.NUMBER)
                                        .description("위도"),
                                fieldWithPath("data.stores[].longitude").type(JsonFieldType.NUMBER)
                                        .description("경도")
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
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("협업지점 아이디"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING)
                                        .description("협업지점 이름")
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
                                fieldWithPath("data.stores[].latitude").type(JsonFieldType.NUMBER)
                                        .description("위도"),
                                fieldWithPath("data.stores[].longitude").type(JsonFieldType.NUMBER)
                                        .description("경도"),
                                subsectionWithPath("data.stores[].imageUrls").description("이미지 URL 목록. 각 요소는 문자열.")
                        )));
    }

    @Test
    @DisplayName("관리자는 새로운 협업지점을 등록할 수 있다.")
    void createStoreTest() throws Exception {
        // given
        CreateStoreRequest store = CreateStoreRequest.builder()
                .name("협업지점명")
                .classification("분류")
                .activateStatus(true)
                .address("주소")
                .umbrellaLocation("우산 위치")
                .businessHours("영업시간")
                .contactNumber("연락처")
                .instagramId("인스타그램 아이디")
                .latitude(33.33)
                .longitude(33.33)
                .imageUrls(List.of("이미지 URL"))
                .build();

        // when

        // then

        mockMvc.perform(post("/stores")
                        .content(objectMapper.writeValueAsString(store))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("store-create-doc",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("협업지점명"),
                                fieldWithPath("classification").type(JsonFieldType.STRING)
                                        .description("분류"),
                                fieldWithPath("activateStatus").type(JsonFieldType.BOOLEAN)
                                        .description("활성화 여부"),
                                fieldWithPath("address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("umbrellaLocation").type(JsonFieldType.STRING)
                                        .description("우산 위치"),
                                fieldWithPath("businessHours").type(JsonFieldType.STRING)
                                        .description("영업시간"),
                                fieldWithPath("contactNumber").type(JsonFieldType.STRING)
                                        .description("연락처"),
                                fieldWithPath("instagramId").type(JsonFieldType.STRING)
                                        .description("인스타그램 아이디"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER)
                                        .description("위도"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER)
                                        .description("경도"),
                                subsectionWithPath("imageUrls").description("이미지 URL 목록. 각 요소는 문자열.")
                        ),
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

    @Test
    @DisplayName("관리자는 협업지점 정보를 수정할 수 있다.")
    void updateStoreTest() throws Exception {
        // given
        UpdateStoreRequest store = UpdateStoreRequest.builder()
                .name("협업지점명")
                .classification("분류")
                .activateStatus(true)
                .address("주소")
                .umbrellaLocation("우산 위치")
                .businessHours("영업시간")
                .contactNumber("연락처")
                .instagramId("인스타그램 아이디")
                .coordinate("네이버 길찾기를 위한 좌표")
                .imageUrls(List.of("이미지 URL"))
                .build();

        // when

        // then

        mockMvc.perform(patch("/stores/{id}", 1L)
                        .content(objectMapper.writeValueAsString(store))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("store-update-doc",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("협업지점명"),
                                fieldWithPath("classification").type(JsonFieldType.STRING)
                                        .description("분류"),
                                fieldWithPath("activateStatus").type(JsonFieldType.BOOLEAN)
                                        .description("활성화 여부"),
                                fieldWithPath("address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("umbrellaLocation").type(JsonFieldType.STRING)
                                        .description("우산 위치"),
                                fieldWithPath("businessHours").type(JsonFieldType.STRING)
                                        .description("영업시간"),
                                fieldWithPath("contactNumber").type(JsonFieldType.STRING)
                                        .description("연락처"),
                                fieldWithPath("instagramId").type(JsonFieldType.STRING)
                                        .description("인스타그램 아이디"),
                                fieldWithPath("coordinate").type(JsonFieldType.STRING)
                                        .description("네이버 길찾기를 위한 좌표"),
                                subsectionWithPath("imageUrls").description("이미지 URL 목록. 각 요소는 문자열.")
                        ),
                        pathParameters(
                                parameterWithName("id").description("협업지점 ID")
                        ),
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

    @Test
    @DisplayName("사용자는 협업지점의 사진을 등록해서 사진의 url을 받을 수 있다.")
    void uploadStorageImages() throws Exception {
        // given
        MockMultipartFile firstFile = new MockMultipartFile("files", "filename-1.jpeg", "text/plain", "some-image".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("files", "filename-2.jpeg", "text/plain", "some-image".getBytes());

        // when

        // then
        mockMvc.perform(multipart("/stores/images")
                        .file(firstFile)
                        .file(secondFile))
                .andExpect(status().isOk())
                .andDo(document("upload-file",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("files").description("The files to upload")
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
                                subsectionWithPath("data.imageUrls").description("이미지 URL 목록. 각 요소는 문자열.")
                        )));
    }

    @Test
    @DisplayName("사용자는 협업지점을 삭제할 수 있다.")
    void deleteStoreTest() throws Exception {
        // given


        // when


        // then
        mockMvc.perform(delete("/stores/{id}", 1L))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("store-delete-doc",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("협업지점 ID")
                        ),
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
