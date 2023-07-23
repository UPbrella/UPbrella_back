package upbrella.be.store.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.store.dto.request.CreateClassificationRequest;
import upbrella.be.store.dto.request.CreateStoreRequest;
import upbrella.be.store.dto.request.CreateSubClassificationRequest;
import upbrella.be.store.dto.request.UpdateStoreRequest;
import upbrella.be.store.service.ClassificationService;
import upbrella.be.store.service.StoreImageService;
import upbrella.be.store.service.StoreMetaService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest extends RestDocsSupport {

    @Mock
    private StoreImageService storeImageService;
    @Mock
    private StoreMetaService storeMetaService;
    @Mock
    private ClassificationService classificationService;


    @Override
    protected Object initController() {
        return new StoreController(storeImageService, storeMetaService, classificationService);
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
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId")
                                        .description("협업 지점 고유번호")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("협업 지점 고유번호"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("이름"),
                                fieldWithPath("businessHours").type(JsonFieldType.STRING)
                                        .description("영업 시간"),
                                fieldWithPath("contactNumber").type(JsonFieldType.STRING)
                                        .description("연락처"),
                                fieldWithPath("address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("availableUmbrellaCount").type(JsonFieldType.NUMBER)
                                        .description("사용가능한 우산 개수"),
                                fieldWithPath("openStatus").type(JsonFieldType.BOOLEAN)
                                        .description("오픈 여부"),
                                fieldWithPath("latitude").type(JsonFieldType.STRING)
                                        .description("위도"),
                                fieldWithPath("longitude").type(JsonFieldType.STRING)
                                        .description("경도")
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
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("latitude")
                                        .description("위도"),
                                parameterWithName("longitude")
                                        .description("경도"),
                                parameterWithName("zoomLevel")
                                        .description("확대 정도")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("stores[]").type(JsonFieldType.ARRAY)
                                        .description("협업 지점 목록"),
                                fieldWithPath("stores[].id").type(JsonFieldType.NUMBER)
                                        .description("협업 지점 고유번호"),
                                fieldWithPath("stores[].name").type(JsonFieldType.STRING)
                                        .description("협업 지점명"),
                                fieldWithPath("stores[].openStatus").type(JsonFieldType.BOOLEAN)
                                        .description("오픈 여부"),
                                fieldWithPath("stores[].latitude").type(JsonFieldType.NUMBER)
                                        .description("위도"),
                                fieldWithPath("stores[].longitude").type(JsonFieldType.NUMBER)
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
                        get("/stores/location/{umbrellaId}", 1)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-find-current-umbrella-store-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("umbrellaId")
                                        .description("우산 고유번호")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("협업 지점 고유번호"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("협업 지점 이름")
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
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("stores[]").type(JsonFieldType.ARRAY)
                                        .description("협업 지점 목록"),
                                fieldWithPath("stores[].id").type(JsonFieldType.NUMBER)
                                        .description("협업 지점 고유번호"),
                                fieldWithPath("stores[].name").type(JsonFieldType.STRING)
                                        .description("협업 지점명"),
                                fieldWithPath("stores[].category").type(JsonFieldType.STRING)
                                        .description("카테고리"),
                                fieldWithPath("stores[].classification").type(JsonFieldType.STRING)
                                        .description("분류"),
                                fieldWithPath("stores[].subClassification").type(JsonFieldType.STRING)
                                        .description("세부 분류"),
                                fieldWithPath("stores[].activateStatus").type(JsonFieldType.BOOLEAN)
                                        .description("활성화 여부"),
                                fieldWithPath("stores[].address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("stores[].umbrellaLocation").type(JsonFieldType.STRING)
                                        .description("우산 위치"),
                                fieldWithPath("stores[].businessHours").type(JsonFieldType.STRING)
                                        .description("영업 시간"),
                                fieldWithPath("stores[].contactNumber").type(JsonFieldType.STRING)
                                        .description("연락처"),
                                fieldWithPath("stores[].instagramId").type(JsonFieldType.STRING)
                                        .description("인스타그램 아이디"),
                                fieldWithPath("stores[].latitude").type(JsonFieldType.NUMBER)
                                        .description("위도"),
                                fieldWithPath("stores[].longitude").type(JsonFieldType.NUMBER)
                                        .description("경도"),
                                fieldWithPath("stores[].content").type(JsonFieldType.STRING)
                                        .description("내용"),
                                fieldWithPath("stores[].imageUrls").type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 목록. 각 요소는 문자열.")
                        )));
    }

//    @Test
    @DisplayName("관리자는 새로운 협업지점을 등록할 수 있다.")
    void createStoreTest() throws Exception {
        // given
        CreateStoreRequest store = CreateStoreRequest.builder()
                .name("협업 지점명")
                .category("카테고리")
//                .classification("분류")
//                .subClassification("세부 분류")
                .activateStatus(true)
                .address("주소")
                .umbrellaLocation("우산 위치")
                .businessHours("영업 시간")
                .contactNumber("연락처")
                .instagramId("인스타그램 아이디")
                .latitude(33.33)
                .longitude(33.33)
                .imageUrls(List.of("이미지 URL"))
                .content("내용")
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
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("협업 지점명"),
                                fieldWithPath("category").type(JsonFieldType.STRING)
                                        .description("카테고리"),
                                fieldWithPath("classification").type(JsonFieldType.STRING)
                                        .description("분류"),
                                fieldWithPath("subClassification").type(JsonFieldType.STRING)
                                        .description("세부 분류"),
                                fieldWithPath("activateStatus").type(JsonFieldType.BOOLEAN)
                                        .description("활성화 여부"),
                                fieldWithPath("address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("umbrellaLocation").type(JsonFieldType.STRING)
                                        .description("우산 위치"),
                                fieldWithPath("businessHours").type(JsonFieldType.STRING)
                                        .description("영업 시간"),
                                fieldWithPath("contactNumber").type(JsonFieldType.STRING)
                                        .description("연락처"),
                                fieldWithPath("instagramId").type(JsonFieldType.STRING)
                                        .description("인스타그램 아이디"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER)
                                        .description("위도"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER)
                                        .description("경도"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("내용"),
                                fieldWithPath("imageUrls[]").type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 목록. 각 요소는 문자열.")
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

        mockMvc.perform(patch("/stores/{storeId}", 1L)
                        .content(objectMapper.writeValueAsString(store))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("store-update-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId")
                                        .description("협업 지점 고유번호")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("협업 지점명"),
                                fieldWithPath("classification").type(JsonFieldType.STRING)
                                        .description("분류"),
                                fieldWithPath("activateStatus").type(JsonFieldType.BOOLEAN)
                                        .description("활성화 여부"),
                                fieldWithPath("address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("umbrellaLocation").type(JsonFieldType.STRING)
                                        .description("우산 위치"),
                                fieldWithPath("businessHours").type(JsonFieldType.STRING)
                                        .description("영업 시간"),
                                fieldWithPath("contactNumber").type(JsonFieldType.STRING)
                                        .description("연락처"),
                                fieldWithPath("instagramId").type(JsonFieldType.STRING)
                                        .description("인스타그램 아이디"),
                                fieldWithPath("coordinate").type(JsonFieldType.STRING)
                                        .description("네이버 길찾기를 위한 좌표"),
                                fieldWithPath("imageUrls").type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 목록. 각 요소는 문자열.")
                        )));
    }

    @Test
    @DisplayName("사용자는 협업지점의 사진을 등록해서 사진의 url을 받을 수 있다.")
    void uploadStorageImages() throws Exception {
        // given
        MockMultipartFile firstFile = new MockMultipartFile("images", "filename-1.jpeg", "text/plain", "some-image".getBytes());

        given(storeImageService.makeRandomId()).willReturn("randomId");
        given(storeImageService.uploadFile(firstFile, 1L, "randomId"))
                .willReturn("https://upbrella-storage/store-image.s3.ap-northeast-2.amazonaws.com/img/filename-1.jpeg");

        // when

        // then
        mockMvc.perform(multipart("/stores/{storeId}/images", 1L)
                        .file(firstFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(document("upload-file",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("협업 지점 고유번호")
                        ),
                        requestParts(
                                partWithName("images").description("협업지점의 사진")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                subsectionWithPath("imageUrls").description("이미지 URL 목록. 각 요소는 문자열.")
                        )));
    }

    @Test
    @DisplayName("사용자는 협업지점을 삭제할 수 있다.")
    void deleteStoreTest() throws Exception {
        // given


        // when


        // then
        mockMvc.perform(delete("/stores/{storeId}", 1L))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("store-delete-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("협업 지점 고유번호")
                        )));
    }

    @Test
    @DisplayName("사용자는 대분류 목록을 조회할 수 있다.")
    void findAllClassificationTest() throws Exception {
        // given


        // when


        // then
        mockMvc.perform(get("/stores/classifications"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("store-find-all-classification-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("classifications[]").type(JsonFieldType.ARRAY)
                                        .description("대분류 목록"),
                                fieldWithPath("classifications[].id").type(JsonFieldType.NUMBER)
                                        .description("대분류 고유번호"),
                                fieldWithPath("classifications[].type").type(JsonFieldType.STRING)
                                        .description("대분류 타입"),
                                fieldWithPath("classifications[].name").type(JsonFieldType.STRING)
                                        .description("대분류 이름"),
                                fieldWithPath("classifications[].latitude").type(JsonFieldType.NUMBER)
                                        .description("대분류 위도"),
                                fieldWithPath("classifications[].longitude").type(JsonFieldType.NUMBER)
                                        .description("대분류 경도")
                        )));
    }

    @Test
    @DisplayName("사용자는 대분류를 추가할 수 있다.")
    void createClassificationTest() throws Exception {
        // given
        CreateClassificationRequest request = CreateClassificationRequest.builder()
                .type("대분류 타입")
                .name("대분류 이름")
                .latitude(33.33)
                .longitude(33.33)
                .build();

        doNothing().when(classificationService).createClassification(any(CreateClassificationRequest.class));

        // when


        // then
        mockMvc.perform(post("/stores/classifications")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("store-create-classification-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("type").type(JsonFieldType.STRING)
                                        .description("대분류 타입"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("대분류 이름"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER)
                                        .description("대분류 위도"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER)
                                        .description("대분류 경도")
                        )));
    }

    @Test
    @DisplayName("사용자는 대분류를 삭제할 수 있다.")
    void deleteClassificationTest() throws Exception {
        // given
        long classificationId = 1L;

        // when
        doNothing().when(classificationService).deleteClassification(classificationId);

        // then
        mockMvc.perform(delete("/stores/classifications/{classificationId}", classificationId))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("store-delete-classification-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("classificationId").description("대분류 고유번호")
                        )));
    }

    @Test
    @DisplayName("사용자는 소분류 목록을 조회할 수 있다.")
    void findAllSubClassificationTest() throws Exception {
        // given


        // when


        // then
        mockMvc.perform(get("/stores/subClassifications"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("store-find-all-sub-classification-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("subClassifications[]").type(JsonFieldType.ARRAY)
                                        .description("소분류 목록"),
                                fieldWithPath("subClassifications[].id").type(JsonFieldType.NUMBER)
                                        .description("소분류 고유번호"),
                                fieldWithPath("subClassifications[].type").type(JsonFieldType.STRING)
                                        .description("소분류 타입"),
                                fieldWithPath("subClassifications[].name").type(JsonFieldType.STRING)
                                        .description("소분류 이름")
                        )));
    }

    @Test
    @DisplayName("사용자는 소분류를 추가할 수 있다.")
    void createSubClassificationTest() throws Exception {
        // given
        CreateSubClassificationRequest request = CreateSubClassificationRequest.builder()
                .type("소분류 타입")
                .name("소분류 이름")
                .build();

        doNothing().when(classificationService).createSubClassification(any(CreateSubClassificationRequest.class));


        // then
        mockMvc.perform(
                post("/stores/subClassifications")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-create-sub-classification-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("type").type(JsonFieldType.STRING)
                                        .description("소분류 타입"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("소분류 이름")
                        )));
    }

    @Test
    @DisplayName("사용자는 소분류를 삭제할 수 있다.")
    void deleteSubClassificationTest() throws Exception {
        // given
        long subClassificationId = 1L;

        doNothing().when(classificationService).deleteClassification(subClassificationId);

        // when


        // then
        mockMvc.perform(
                delete("/stores/subClassifications/{subClassificationId}", subClassificationId)
        ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-delete-sub-classification-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("subClassificationId").description("소분류 고유번호")
                        )));
    }
}
