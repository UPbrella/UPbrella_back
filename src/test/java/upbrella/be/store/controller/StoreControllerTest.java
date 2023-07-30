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
import upbrella.be.store.dto.request.*;
import upbrella.be.store.dto.response.*;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.DayOfWeek;
import upbrella.be.store.repository.StoreMetaRepository;
import upbrella.be.store.service.ClassificationService;
import upbrella.be.store.service.StoreDetailService;
import upbrella.be.store.service.StoreImageService;
import upbrella.be.store.service.StoreMetaService;

import java.time.LocalTime;
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
    @Mock
    private StoreMetaRepository storeMetaRepository;
    @Mock
    private StoreDetailService storeDetailService;

    @Override
    protected Object initController() {
        return new StoreController(storeImageService, storeMetaService, classificationService, storeMetaRepository, storeDetailService);
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
        final double latitudeFrom = 37.5666103;
        final double latitudeTo = 77.5666103;
        final double longitudeFrom = 36.9783882;
        final double longitudeTo = 126.9783882;

        given(storeMetaService.findStoresInCurrentMap(any(CoordinateRequest.class)))
                .willReturn(
                        List.of(
                                SingleCurrentLocationStoreResponse.builder()
                                        .id(1)
                                        .name("업브렐라")
                                        .latitude(37.503716)
                                        .longitude(127.053718)
                                        .openStatus(true)
                                        .build()));

        // when & then
        mockMvc.perform(
                        get("/stores/location")
                                .param("latitudeFrom", String.valueOf(latitudeFrom))
                                .param("latitudeTo", String.valueOf(latitudeTo))
                                .param("longitudeFrom", String.valueOf(longitudeFrom))
                                .param("longitudeTo", String.valueOf(longitudeTo))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("store-find-by-location-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("latitudeFrom")
                                        .description("위도 경계 시작"),
                                parameterWithName("latitudeTo")
                                        .description("위도 경계 종료"),
                                parameterWithName("longitudeFrom")
                                        .description("경도 경계 시작"),
                                parameterWithName("longitudeTo")
                                        .description("경도 경계 종료")
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
        given(storeMetaRepository.findAllStores())
                .willReturn(List.of(SingleStoreResponse.builder()
                        .name("모티브 카페 신촌점")
                        .category("카페 티저트")
                        .content("모티브 카페")
                        .classification(SingleClassificationResponse.builder()
                                .id(1L)
                                .type("classification")
                                .name("신촌")
                                .latitude(33.33)
                                .longitude(33.33)
                                .build())
                        .subClassification(SingleSubClassificationResponse.builder()
                                .id(1L)
                                .type("subClassification")
                                .name("신촌")
                                .build())
                        .activateStatus(true)
                        .address("주소")
                        .umbrellaLocation("가게 앞")
                        .businessHour("연중 무휴")
                        .contactNumber("010-0000-0000")
                        .instagramId("instagramId")
                        .latitude(33.33)
                        .longitude(33.33)
                        .imageUrls(List.of(SingleImageUrlResponse.builder()
                                .id(1L)
                                .imageUrl("url")
                                .build()))
                        .password("비밀번호")
                        .businessHours(
                                List.of(
                                        SingleBusinessHourResponse.builder()
                                                .date(DayOfWeek.MONDAY)
                                                .openAt(LocalTime.of(10, 0))
                                                .closeAt(LocalTime.of(20, 0))
                                                .build(),
                                        SingleBusinessHourResponse.builder()
                                                .date(DayOfWeek.TUESDAY)
                                                .openAt(LocalTime.of(10, 0))
                                                .closeAt(LocalTime.of(20, 0))
                                                .build(),
                                        SingleBusinessHourResponse.builder()
                                                .date(DayOfWeek.WEDNESDAY)
                                                .openAt(LocalTime.of(10, 0))
                                                .closeAt(LocalTime.of(20, 0))
                                                .build(),
                                        SingleBusinessHourResponse.builder()
                                                .date(DayOfWeek.THURSDAY)
                                                .openAt(LocalTime.of(10, 0))
                                                .closeAt(LocalTime.of(20, 0))
                                                .build(),
                                        SingleBusinessHourResponse.builder()
                                                .date(DayOfWeek.FRIDAY)
                                                .openAt(LocalTime.of(10, 0))
                                                .closeAt(LocalTime.of(20, 0))
                                                .build(),
                                        SingleBusinessHourResponse.builder()
                                                .date(DayOfWeek.SATURDAY)
                                                .openAt(LocalTime.of(10, 0))
                                                .closeAt(LocalTime.of(20, 0))
                                                .build(),
                                        SingleBusinessHourResponse.builder()
                                                .date(DayOfWeek.SUNDAY)
                                                .openAt(LocalTime.of(10, 0))
                                                .closeAt(LocalTime.of(20, 0))
                                                .build())
                        )
                        .build()));

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
                                fieldWithPath("stores[].imageUrls").type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 목록"),
                                fieldWithPath("stores[].imageUrls[].id").type(JsonFieldType.NUMBER)
                                        .description("이미지 고유번호"),
                                fieldWithPath("stores[].imageUrls[].imageUrl").type(JsonFieldType.STRING)
                                        .description("이미지 URL"),
                                fieldWithPath("stores[].password").type(JsonFieldType.STRING)
                                        .description("비밀번호"),
                                fieldWithPath("stores[].businessHours").type(JsonFieldType.ARRAY)
                                        .description("영업 시간"),
                                fieldWithPath("stores[].businessHours[].date").type(JsonFieldType.STRING)
                                        .description("영업 요일"),
                                fieldWithPath("stores[].businessHours[].openAt").type(JsonFieldType.STRING)
                                        .description("오픈 시간"),
                                fieldWithPath("stores[].businessHours[].closeAt").type(JsonFieldType.STRING)
                                        .description("마감 시간")
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
                .umbrellaLocation("우산 위치")
                .businessHour("영업 시간")
                .contactNumber("연락처")
                .instagramId("인스타그램 아이디")
                .latitude(33.33)
                .longitude(33.33)
                .content("내용")
                .password("비밀번호")
                .businessHours(
                        AllBusinessHourRequest.builder()
                                .storeMetaId(1L)
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
                                .build())
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
                                fieldWithPath("classificationId").type(JsonFieldType.NUMBER)
                                        .description("대분류 아이디"),
                                fieldWithPath("subClassificationId").type(JsonFieldType.NUMBER)
                                        .description("소분류"),
                                fieldWithPath("activateStatus").type(JsonFieldType.BOOLEAN)
                                        .description("활성화 여부"),
                                fieldWithPath("address").type(JsonFieldType.STRING)
                                        .description("주소"),
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
                                fieldWithPath("businessHours").type(JsonFieldType.OBJECT)
                                        .description("영업 시간"),
                                fieldWithPath("businessHours.storeMetaId").type(JsonFieldType.NUMBER)
                                        .description("협업 지점 고유번호"),
                                fieldWithPath("businessHours.businessHours[].date").type(JsonFieldType.STRING)
                                        .description("영업 요일"),
                                fieldWithPath("businessHours.businessHours[].openAt").type(JsonFieldType.STRING)
                                        .description("오픈 시간"),
                                fieldWithPath("businessHours.businessHours[].closeAt").type(JsonFieldType.STRING)
                                        .description("마감 시간")
                        )));
    }

    @Test
    @DisplayName("관리자는 협업지점 정보를 수정할 수 있다.")
    void updateStoreTest() throws Exception {
        // given
        CreateStoreRequest store = CreateStoreRequest.builder()
                .name("협업 지점명")
                .category("카테고리")
                .classificationId(1L)
                .subClassificationId(2L)
                .activateStatus(true)
                .address("주소")
                .umbrellaLocation("우산 위치")
                .businessHour("영업 시간")
                .contactNumber("연락처")
                .instagramId("인스타그램 아이디")
                .latitude(33.33)
                .longitude(33.33)
                .content("내용")
                .password("비밀번호")
                .businessHours(
                        AllBusinessHourRequest.builder()
                                .storeMetaId(1L)
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
                                .build())
                .build();
        long storeId = 1L;

        doNothing().when(storeDetailService).updateStore(any(Long.class), any(CreateStoreRequest.class));

        // then

        mockMvc.perform(patch("/stores/{storeId}", storeId)
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
                                fieldWithPath("activateStatus").type(JsonFieldType.BOOLEAN)
                                        .description("활성화 여부"),
                                fieldWithPath("address").type(JsonFieldType.STRING)
                                        .description("주소"),
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
                                fieldWithPath("businessHours").type(JsonFieldType.OBJECT)
                                        .description("영업 시간"),
                                fieldWithPath("businessHours.storeMetaId").type(JsonFieldType.NUMBER)
                                        .description("협업 지점 고유번호"),
                                fieldWithPath("businessHours.businessHours[].date").type(JsonFieldType.STRING)
                                        .description("영업 요일"),
                                fieldWithPath("businessHours.businessHours[].openAt").type(JsonFieldType.STRING)
                                        .description("오픈 시간"),
                                fieldWithPath("businessHours.businessHours[].closeAt").type(JsonFieldType.STRING)
                                        .description("마감 시간")
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
        long storeMetaId = 1L;

        doNothing().when(storeMetaService).deleteStoreMeta(storeMetaId);


        // then
        mockMvc.perform(delete("/stores/{storeId}", storeMetaId))
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
        given(classificationService.findAllClassification("classification"))
                .willReturn(AllClassificationResponse.builder()
                        .classifications(List.of(
                                SingleClassificationResponse.builder()
                                        .id(1L)
                                        .type("대분류 타입")
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
                .type("대분류 타입")
                .name("대분류 이름")
                .latitude(33.33)
                .longitude(33.33)
                .build();

        given(classificationService.createClassification(any(CreateClassificationRequest.class))).willReturn(Classification.builder().build());

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
        given(classificationService.findAllSubClassification("subClassification"))
                .willReturn(AllSubClassificationResponse.builder()
                        .subClassifications(List.of(
                                SingleSubClassificationResponse.builder()
                                        .id(1L)
                                        .type("소분류 타입")
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
                .type("소분류 타입")
                .name("소분류 이름")
                .build();

        given(classificationService.createSubClassification(any(CreateSubClassificationRequest.class))).willReturn(Classification.builder().build());

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
