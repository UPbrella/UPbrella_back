package upbrella.be.store.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.multipart.MultipartFile;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.store.dto.request.*;
import upbrella.be.store.dto.response.*;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.ClassificationType;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.service.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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

//1hour
@ExtendWith(MockitoExtension.class)
class StoreControllerTest extends RestDocsSupport {
    @Mock
    private StoreImageService storeImageService;
    @Mock
    private StoreMetaService storeMetaService;
    @Mock
    private ClassificationService classificationService;
    @Mock
    private StoreDetailService storeDetailService;
    @Mock
    private BusinessHourService businessHourService;

    @Override
    protected Object initController() {

        return new StoreController(storeImageService, storeMetaService, classificationService, storeDetailService, businessHourService);
    }

    @Test
    @DisplayName("스토어의 아이디로 스토어를 상세조회할 수 있다.")
    void findStoreByIdTest() throws Exception {
        // given
        StoreFindByIdResponse storeFindByIdResponse = StoreFindByIdResponse.builder()
                .id(1)
                .name("업브렐라")
                .category("카페")
                .umbrellaLocation("가게 앞")
                .instaUrl("인스타 ID 예시")
                .businessHours("09:00 ~ 18:00")
                .contactNumber("010-0000-0000")
                .address("서울특별시 강남구 테헤란로 427")
                .description("우리 카페는 맛있고 뷰가 좋습니다.")
                .availableUmbrellaCount(10)
                .openStatus(true)
                .latitude(37.503716)
                .longitude(127.053718)
                .imageUrls(List.of("https://upbrella-store-image.s3.ap-northeast-2.amazonaws.com/1.jpg",
                        "https://upbrella-store-image.s3.ap-northeast-2.amazonaws.com/2.jpg",
                        "https://upbrella-store-image.s3.ap-northeast-2.amazonaws.com/3.jpg"))
                .build();

        given(storeDetailService.findStoreDetailByStoreId(2L))
                .willReturn(storeFindByIdResponse);

        // when & then
        mockMvc.perform(
                        get("/stores/{storeId}", 2L)
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
                                fieldWithPath("category").type(JsonFieldType.STRING)
                                        .description("카테고리"),
                                fieldWithPath("availableUmbrellaCount").type(JsonFieldType.NUMBER)
                                        .description("사용가능한 우산 개수"),
                                fieldWithPath("openStatus").type(JsonFieldType.BOOLEAN)
                                        .description("오픈 여부"),
                                fieldWithPath("businessHours").type(JsonFieldType.STRING)
                                        .description("영업 시간"),
                                fieldWithPath("contactNumber").type(JsonFieldType.STRING)
                                        .description("연락처"),
                                fieldWithPath("instaUrl").type(JsonFieldType.STRING)
                                        .description("인스타그램 URL"),
                                fieldWithPath("address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("umbrellaLocation").type(JsonFieldType.STRING)
                                        .description("우산 위치"),
                                fieldWithPath("description").type(JsonFieldType.STRING)
                                        .description("소개 글"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER)
                                        .description("위도"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER)
                                        .description("경도"),
                                fieldWithPath("imageUrls[]").type(JsonFieldType.ARRAY)
                                        .description("이미지 목록")
                        )));
    }

    @Test
    @DisplayName("사용자는 협업지점 대분류 ID로 협업지점 목록을 조회할 수 있다.")
    void test() throws Exception {

        // given
        final long classificationId = 1L;

        given(storeMetaService.findAllStoresByClassification(anyLong(), any(LocalDateTime.class)))
                .willReturn(
                        AllCurrentLocationStoreResponse.builder()
                                .stores(
                                        List.of(
                                                SingleCurrentLocationStoreResponse.builder()
                                                        .id(1)
                                                        .name("업브렐라")
                                                        .latitude(37.503716)
                                                        .longitude(127.053718)
                                                        .openStatus(true)
                                                        .rentableUmbrellasCount(3)
                                                        .build()))
                                .build());

        // when & then
        mockMvc.perform(
                        get("/stores/classification/{classificationId}", classificationId)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-find-by-location-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("classificationId")
                                        .description("대분류 고유번호")
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
                                        .description("경도"),
                                fieldWithPath("stores[].rentableUmbrellasCount").type(JsonFieldType.NUMBER)
                                        .description("대여 가능 우산 개수")
                        )));
    }

    @Test
    @DisplayName("우산의 현위치를 조회할 수 있다. ")
    void findCurrentUmbrellaStoreTest() throws Exception {
        // given
        CurrentUmbrellaStoreResponse currentUmbrellaStoreResponse = new CurrentUmbrellaStoreResponse(1L, "모티브 카페 신촌점");
        given(storeMetaService.findCurrentStoreIdByUmbrella(2L))
                .willReturn(currentUmbrellaStoreResponse);

        // when & then
        mockMvc.perform(
                        get("/stores/location/{umbrellaId}", 2L)
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
        given(storeDetailService.findAllStores())
                .willReturn(List.of(SingleStoreResponse.builder()
                        .name("모티브 카페 신촌점")
                        .category("카페 티저트")
                        .content("모티브 카페")
                        .classification(SingleClassificationResponse.builder()
                                .id(1L)
                                .type(ClassificationType.CLASSIFICATION)
                                .name("신촌")
                                .latitude(33.33)
                                .longitude(33.33)
                                .build())
                        .subClassification(SingleSubClassificationResponse.builder()
                                .id(1L)
                                .type(ClassificationType.SUB_CLASSIFICATION)
                                .name("신촌")
                                .build())
                        .activateStatus(true)
                        .address("주소")
                        .addressDetail("상세주소")
                        .umbrellaLocation("가게 앞")
                        .businessHour("연중 무휴")
                        .contactNumber("010-0000-0000")
                        .instagramId("instagramId")
                        .latitude(33.33)
                        .longitude(33.33)
                        .password("비밀번호")
                        .build()));

        // when


        // then
        mockMvc.perform(
                        get("/admin/stores")
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
                                fieldWithPath("stores[].classification").type(JsonFieldType.OBJECT)
                                        .description("대분류"),
                                fieldWithPath("stores[].classification.id").type(JsonFieldType.NUMBER)
                                        .description("대분류 아이디"),
                                fieldWithPath("stores[].classification.type").type(JsonFieldType.STRING)
                                        .description("대분류 타입"),
                                fieldWithPath("stores[].classification.name").type(JsonFieldType.STRING)
                                        .description("대분류 이름"),
                                fieldWithPath("stores[].classification.latitude").type(JsonFieldType.NUMBER)
                                        .description("대분류 위도"),
                                fieldWithPath("stores[].classification.longitude").type(JsonFieldType.NUMBER)
                                        .description("대분류 경도"),
                                fieldWithPath("stores[].subClassification").type(JsonFieldType.OBJECT)
                                        .description("소분류"),
                                fieldWithPath("stores[].subClassification.id").type(JsonFieldType.NUMBER)
                                        .description("소분류 아이디"),
                                fieldWithPath("stores[].subClassification.type").type(JsonFieldType.STRING)
                                        .description("소분류 타입"),
                                fieldWithPath("stores[].subClassification.name").type(JsonFieldType.STRING)
                                        .description("소분류 이름"),
                                fieldWithPath("stores[].activateStatus").type(JsonFieldType.BOOLEAN)
                                        .description("활성화 여부"),
                                fieldWithPath("stores[].address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("stores[].addressDetail").type(JsonFieldType.STRING)
                                        .description("상세 주소"),
                                fieldWithPath("stores[].umbrellaLocation").type(JsonFieldType.STRING)
                                        .description("우산 위치"),
                                fieldWithPath("stores[].businessHour").type(JsonFieldType.STRING)
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
                                fieldWithPath("stores[].password").type(JsonFieldType.STRING)
                                        .description("비밀번호")
                        )));
    }

    @Test
    @DisplayName("관리자는 새로운 협업지점을 등록할 수 있다.")
    void createStoreTest() throws Exception {
        // given
        CreateStoreRequest store = CreateStoreRequest.builder()
                .name("협업 지점명")
                .category("카테고리")
                .classificationId(1L)
                .subClassificationId(2L)
                .activateStatus(true)
                .address("주소")
                .addressDetail("상세주소")
                .umbrellaLocation("우산 위치")
                .businessHour("영업 시간")
                .contactNumber("연락처")
                .instagramId("인스타그램 아이디")
                .latitude(33.33)
                .longitude(33.33)
                .content("내용")
                .password("비밀번호")
                .businessHours(
                        List.of(
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.MONDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.TUESDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.WEDNESDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.THURSDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.FRIDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.SATURDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.SUNDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build()))
                .build();

        // when

        // then

        mockMvc.perform(post("/admin/stores")
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
                                fieldWithPath("classificationId").type(JsonFieldType.NUMBER)
                                        .description("대분류 아이디"),
                                fieldWithPath("subClassificationId").type(JsonFieldType.NUMBER)
                                        .description("소분류"),
                                fieldWithPath("activateStatus").type(JsonFieldType.BOOLEAN)
                                        .description("활성화 여부"),
                                fieldWithPath("address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("addressDetail").type(JsonFieldType.STRING)
                                        .description("상세 주소"),
                                fieldWithPath("umbrellaLocation").type(JsonFieldType.STRING)
                                        .description("우산 위치"),
                                fieldWithPath("businessHour").type(JsonFieldType.STRING)
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
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("비밀번호"),
                                fieldWithPath("businessHours").type(JsonFieldType.ARRAY)
                                        .description("영업 시간"),
                                fieldWithPath("businessHours[].date").type(JsonFieldType.STRING)
                                        .description("영업 요일"),
                                fieldWithPath("businessHours[].openAt").type(JsonFieldType.STRING)
                                        .description("오픈 시간"),
                                fieldWithPath("businessHours[].closeAt").type(JsonFieldType.STRING)
                                        .description("마감 시간")
                        )));
    }

    @Test
    @DisplayName("관리자는 협업지점 정보를 수정할 수 있다.")
    void updateStoreTest() throws Exception {
        // given
        UpdateStoreRequest store = UpdateStoreRequest.builder()
                .name("협업 지점명")
                .category("카테고리")
                .classificationId(1L)
                .subClassificationId(2L)
                .address("주소")
                .addressDetail("상세주소")
                .umbrellaLocation("우산 위치")
                .businessHour("영업 시간")
                .contactNumber("연락처")
                .instagramId("인스타그램 아이디")
                .latitude(33.33)
                .longitude(33.33)
                .content("내용")
                .password("비밀번호")
                .businessHours(
                        List.of(
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.MONDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.TUESDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.WEDNESDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.THURSDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.FRIDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.SATURDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build(),
                                SingleBusinessHourRequest.builder()
                                        .date(DayOfWeek.SUNDAY)
                                        .openAt(LocalTime.of(10, 0))
                                        .closeAt(LocalTime.of(20, 0))
                                        .build()))
                .build();
        long storeId = 1L;

        doNothing().when(storeDetailService).updateStore(any(Long.class), any(UpdateStoreRequest.class));

        // then

        mockMvc.perform(patch("/admin/stores/{storeId}", storeId)
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
                                fieldWithPath("category").type(JsonFieldType.STRING)
                                        .description("카테고리"),
                                fieldWithPath("classificationId").type(JsonFieldType.NUMBER)
                                        .description("대분류 아이디"),
                                fieldWithPath("subClassificationId").type(JsonFieldType.NUMBER)
                                        .description("소분류"),
                                fieldWithPath("address").type(JsonFieldType.STRING)
                                        .description("주소"),
                                fieldWithPath("addressDetail").type(JsonFieldType.STRING)
                                        .description("상세 주소"),
                                fieldWithPath("umbrellaLocation").type(JsonFieldType.STRING)
                                        .description("우산 위치"),
                                fieldWithPath("businessHour").type(JsonFieldType.STRING)
                                        .description("영업 시간"),
                                fieldWithPath("contactNumber").type(JsonFieldType.STRING)
                                        .description("연락처")
                                        .optional(),
                                fieldWithPath("instagramId").type(JsonFieldType.STRING)
                                        .description("인스타그램 아이디")
                                        .optional(),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER)
                                        .description("위도"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER)
                                        .description("경도"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("내용"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("비밀번호")
                                        .optional(),
                                fieldWithPath("businessHours").type(JsonFieldType.ARRAY)
                                        .description("영업 시간"),
                                fieldWithPath("businessHours[].date").type(JsonFieldType.STRING)
                                        .description("영업 요일"),
                                fieldWithPath("businessHours[].openAt").type(JsonFieldType.STRING)
                                        .description("오픈 시간"),
                                fieldWithPath("businessHours[].closeAt").type(JsonFieldType.STRING)
                                        .description("마감 시간")
                        )));
    }

    @Test
    @DisplayName("사용자는 협업지점의 사진을 등록할 수 있다.")
    void uploadStorageImages() throws Exception {
        // given
        MockMultipartFile firstFile = new MockMultipartFile("image", "filename-1.jpeg", "text/plain", "some-image".getBytes());
        given(storeImageService.uploadFile(any(MultipartFile.class), any(Long.class), nullable(String.class)))
                .willReturn("url");

        // when

        // then
        mockMvc.perform(multipart("/admin/stores/{storeId}/images", 1L)
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
                                partWithName("image").description("협업지점의 사진")
                        )));
    }

    @Test
    @DisplayName("사용자는 협업지점의 사진을 삭제할 수 있다.")
    void deleteStoreImagesTest() throws Exception {
        // given
        long imageId = 1L;

        // when
        doNothing().when(storeImageService).deleteFile(imageId);

        // then
        mockMvc.perform(delete("/admin/stores/images/{imageId}", imageId))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("store-delete-images-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("imageId").description("협업 지점 사진 고유번호")
                        )));
    }

    @Test
    @DisplayName("사용자는 협업지점을 삭제할 수 있다.")
    void deleteStoreTest() throws Exception {
        // given
        long storeMetaId = 1L;

        doNothing().when(storeMetaService).deleteStoreMeta(storeMetaId);


        // then
        mockMvc.perform(delete("/admin/stores/{storeId}", storeMetaId))
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
        given(classificationService.findAllClassification())
                .willReturn(AllClassificationResponse.builder()
                        .classifications(List.of(
                                SingleClassificationResponse.builder()
                                        .id(1L)
                                        .type(ClassificationType.CLASSIFICATION)
                                        .name("대분류 이름")
                                        .latitude(33.33)
                                        .longitude(33.33)
                                        .build()
                        ))
                        .build());

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
                .name("대분류 이름")
                .latitude(33.33)
                .longitude(33.33)
                .build();

        given(classificationService.createClassification(any(CreateClassificationRequest.class))).willReturn(Classification.builder().build());

        // then
        mockMvc.perform(post("/admin/stores/classifications")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("store-create-classification-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
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
        mockMvc.perform(delete("/admin/stores/classifications/{classificationId}", classificationId))
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
        given(classificationService.findAllSubClassification())
                .willReturn(AllSubClassificationResponse.builder()
                        .subClassifications(List.of(
                                SingleSubClassificationResponse.builder()
                                        .id(1L)
                                        .type(ClassificationType.SUB_CLASSIFICATION)
                                        .name("소분류 이름")
                                        .build()
                        ))
                        .build());

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
                .name("소분류 이름")
                .build();

        given(classificationService.createSubClassification(any(CreateSubClassificationRequest.class))).willReturn(Classification.builder().build());

        // then
        mockMvc.perform(
                        post("/admin/stores/subClassifications")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-create-sub-classification-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("소분류 이름")
                        )));
    }

    @Test
    @DisplayName("사용자는 소분류를 삭제할 수 있다.")
    void deleteSubClassificationTest() throws Exception {
        // given
        long subClassificationId = 1L;

        doNothing().when(classificationService).deleteSubClassification(subClassificationId);

        // when


        // then
        mockMvc.perform(
                        delete("/admin/stores/subClassifications/{subClassificationId}", subClassificationId)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-delete-sub-classification-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("subClassificationId").description("소분류 고유번호")
                        )));
    }

    @Test
    @DisplayName("사용자는 협업지점 소개 페이지를 조회할 수 있다.")
    void findStoreIntroductionTest() throws Exception {

        // given
        StoreIntroductionsResponseByClassification storeIntroductionsResponseByClassification = StoreIntroductionsResponseByClassification
                .builder()
                .subClassificationId(1)
                .stores(List.of(SingleStoreIntroductionResponse.of(1L, "가게 이름", "가게 카테고리", "가게 썸네일")))
                .build();

        AllStoreIntroductionResponse response = AllStoreIntroductionResponse.builder()
                .storesByClassification(List.of(storeIntroductionsResponseByClassification))
                .build();

        given(storeDetailService.findAllStoreIntroductions()).willReturn(response);

        // when & then
        mockMvc.perform(
                        get("/stores/introductions")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-find-store-introduction-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("storesByClassification[]").type(JsonFieldType.ARRAY)
                                        .description("전체 협업 지점 소개 목록"),
                                fieldWithPath("storesByClassification[].subClassificationId").type(JsonFieldType.NUMBER)
                                        .description("협업 지점 소분류 고유번호"),
                                fieldWithPath("storesByClassification[].stores[]").type(JsonFieldType.ARRAY)
                                        .description("소분류별 협업 지점 목록"),
                                fieldWithPath("storesByClassification[].stores[].id").type(JsonFieldType.NUMBER)
                                        .description("가게 고유번호"),
                                fieldWithPath("storesByClassification[].stores[].name").type(JsonFieldType.STRING)
                                        .description("가게 이름"),
                                fieldWithPath("storesByClassification[].stores[].category").type(JsonFieldType.STRING)
                                        .description("가게 카테고리"),
                                fieldWithPath("storesByClassification[].stores[].thumbnail").type(JsonFieldType.STRING)
                                        .description("가게 썸네일")
                        )));
    }

    @Test
    @DisplayName("사용자는 협업지점의 활성화 여부 상태를 변경할 수 있다.")
    void updateActivateStatusTest() throws Exception {
        // given
        long storeId = 1L;
        doNothing().when(storeMetaService).activateStoreStatus(storeId);

        // when
        storeMetaService.activateStoreStatus(storeId);

        // then
        mockMvc.perform(
                        patch("/admin/stores/{storeId}/activate", storeId)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-update-activate-status-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("협업 지점 고유번호")
                        )));
    }

    @Test
    @DisplayName("사용자는 협업지점의 활성화 여부 상태를 변경할 수 있다.")
    void inactivateStoreStatus() throws Exception {
        // given
        long storeId = 1L;
        doNothing().when(storeMetaService).inactivateStoreStatus(storeId);

        // when
        storeMetaService.inactivateStoreStatus(storeId);

        // then
        mockMvc.perform(
                        patch("/admin/stores/{storeId}/inactivate", storeId)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-update-inactivate-status-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("협업 지점 고유번호")
                        )));
    }

    @Test
    @DisplayName("협업지점의 이미지들을 조회할 수 있다.")
    void findAllImages() throws Exception {
        // given
        given(storeImageService.findAllImages(1L))
                .willReturn(AllImageUrlResponse
                        .builder()
                        .storeId(1L)
                        .images(List.of(SingleImageUrlResponse.builder()
                                .id(1L)
                                .imageUrl("url")
                                .build()))
                        .build());

        // when
        storeImageService.findAllImages(1L);

        // then
        mockMvc.perform(
                        get("/admin/stores/{storeId}/images", 1L)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-find-all-images-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("협업 지점 고유번호")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER)
                                        .description("협업 지점 고유번호"),
                                fieldWithPath("images[]").type(JsonFieldType.ARRAY)
                                        .description("협업 지점 이미지 목록"),
                                fieldWithPath("images[].id").type(JsonFieldType.NUMBER)
                                        .description("협업 지점 이미지 고유번호"),
                                fieldWithPath("images[].imageUrl").type(JsonFieldType.STRING)
                                        .description("협업 지점 이미지 URL")
                        )));
    }

    @Test
    @DisplayName("협업지점의 영업시간들을 조회할 수 있다.")
    void findAllBusinessHours() {
        // given


        // when


        // then

    }
}
