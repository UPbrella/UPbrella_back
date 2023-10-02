package upbrella.be.umbrella.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import upbrella.be.config.FixtureBuilderFactory;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.store.controller.StoreExceptionHandler;
import upbrella.be.store.exception.NonExistingStoreMetaException;
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest;
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
import upbrella.be.umbrella.dto.response.UmbrellaStatisticsResponse;
import upbrella.be.umbrella.exception.ExistingUmbrellaUuidException;
import upbrella.be.umbrella.exception.NonExistingUmbrellaException;
import upbrella.be.umbrella.service.UmbrellaService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static upbrella.be.config.FixtureBuilderFactory.buildInteger;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse;

@ExtendWith(MockitoExtension.class)
public class UmbrellaControllerTest extends RestDocsSupport {

    @Mock
    private UmbrellaService umbrellaService;

    @Override
    protected Object initController() {
        return new UmbrellaController(umbrellaService);
    }

    @DisplayName("사용자는 전체 우산 현황을 조회할 수 있다.")
    @Test
    void showAllUmbrellasTest() throws Exception {

        // given
        List<UmbrellaResponse> umbrellaResponseList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            umbrellaResponseList.add(FixtureBuilderFactory.builderUmbrellaResponses()
                    .sample());
        }

        Pageable pageable = PageRequest.of(0, 5);
        given(umbrellaService.findAllUmbrellas(pageable))
                .willReturn(umbrellaResponseList);

        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("page", "0");
        info.add("size", "5");

        // when & then
        mockMvc.perform(
                        get("/admin/umbrellas")
                                .params(info)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("show-all-umbrellas-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("umbrellaResponsePage[]").type(JsonFieldType.ARRAY)
                                        .description("우산 목록"),
                                fieldWithPath("umbrellaResponsePage[].id").type(JsonFieldType.NUMBER)
                                        .description("우산 고유번호"),
                                fieldWithPath("umbrellaResponsePage[].historyId").type(JsonFieldType.NUMBER)
                                        .description("현재 대여 내역 고유번호")
                                        .optional(),
                                fieldWithPath("umbrellaResponsePage[].storeMetaId").type(JsonFieldType.NUMBER)
                                        .description("보관 지점 고유번호"),
                                fieldWithPath("umbrellaResponsePage[].storeName").type(JsonFieldType.STRING)
                                        .description("보관 지점 이름"),
                                fieldWithPath("umbrellaResponsePage[].uuid").type(JsonFieldType.NUMBER)
                                        .description("우산 관리번호"),
                                fieldWithPath("umbrellaResponsePage[].rentable").type(JsonFieldType.BOOLEAN)
                                        .description("대여 가능 상태"),
                                fieldWithPath("umbrellaResponsePage[].etc").type(JsonFieldType.STRING)
                                        .description("기타 특이 사항")
                        )));
    }

    @DisplayName("사용자는 지점 우산 현황을 조회할 수 있다.")
    @Test
    void showUmbrellasByStoreIdTest() throws Exception {

        // given
        int storeId = buildInteger(100);
        List<UmbrellaResponse> umbrellaResponseList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            umbrellaResponseList.add(FixtureBuilderFactory.builderUmbrellaResponses()
                    .set("storeMetaId", storeId)
                    .sample());
        }

        Pageable pageable = PageRequest.of(0, 5);

        given(umbrellaService.findUmbrellasByStoreId(storeId, pageable))
                .willReturn(umbrellaResponseList);

        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("page", "0");
        info.add("size", "5");

        // when & then
        mockMvc.perform(
                        get("/admin/umbrellas/{storeId}", storeId)
                                .params(info)
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("show-umbrellas-by-store-id-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("지점 고유번호")
                        ),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("umbrellaResponsePage[]").type(JsonFieldType.ARRAY)
                                        .description("우산 목록"),
                                fieldWithPath("umbrellaResponsePage[].id").type(JsonFieldType.NUMBER)
                                        .description("우산 고유번호"),
                                fieldWithPath("umbrellaResponsePage[].historyId").type(JsonFieldType.NUMBER)
                                        .description("현재 대여 내역 고유번호")
                                        .optional(),
                                fieldWithPath("umbrellaResponsePage[].storeMetaId").type(JsonFieldType.NUMBER)
                                        .description("보관 지점 고유 번호"),
                                fieldWithPath("umbrellaResponsePage[].storeName").type(JsonFieldType.STRING)
                                        .description("보관 지점 이름"),
                                fieldWithPath("umbrellaResponsePage[].uuid").type(JsonFieldType.NUMBER)
                                        .description("우산 관리번호"),
                                fieldWithPath("umbrellaResponsePage[].rentable").type(JsonFieldType.BOOLEAN)
                                        .description("대여 가능 상태"),
                                fieldWithPath("umbrellaResponsePage[].etc").type(JsonFieldType.STRING)
                                        .description("기타 특이 사항")
                        )));
    }

    @Nested
    @DisplayName("사용자는 우산의 정보로 POST 요청을 보내")
    class AddUmbrella {

        @DisplayName("새로운 우산을 추가할 수 있다.")
        @Test
        void success() throws Exception {

            // given
            UmbrellaCreateRequest umbrellaCreateRequest = FixtureBuilderFactory.builderUmbrellaCreateRequest()
                    .sample();
            doNothing().when(umbrellaService).addUmbrella(refEq(umbrellaCreateRequest));

            // when & then
            mockMvc.perform(
                            post("/admin/umbrellas")
                                    .content(objectMapper.writeValueAsString(umbrellaCreateRequest))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("add-umbrellas-doc",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("uuid").type(JsonFieldType.NUMBER)
                                            .description("우산 관리번호"),
                                    fieldWithPath("storeMetaId").type(JsonFieldType.NUMBER)
                                            .description("지점 고유번호"),
                                    fieldWithPath("rentable").type(JsonFieldType.BOOLEAN)
                                            .description("대여 가능 여부"),
                                    fieldWithPath("etc").type(JsonFieldType.STRING)
                                            .optional()
                                            .description("기타 특이 사항"))));
        }

        @DisplayName("우산 관리번호가 이미 존재하면 400 에러가 반환된다.")
        @Test
        void existingUmbrellaUuid() throws Exception {

            // given
            UmbrellaCreateRequest umbrellaCreateRequest = FixtureBuilderFactory.builderUmbrellaCreateRequest()
                    .sample();

            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UmbrellaExceptionHandler());

            doThrow(new ExistingUmbrellaUuidException("[ERROR] 이미 존재하는 우산 관리번호입니다.")).when(umbrellaService).addUmbrella(refEq(umbrellaCreateRequest));

            // when & then
            mockMvc.perform(
                            post("/admin/umbrellas")
                                    .content(objectMapper.writeValueAsString(umbrellaCreateRequest))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(ExistingUmbrellaUuidException.class));
        }
    }


    @Nested
    @DisplayName("사용자는 우산의 정보로 PATCH 요청을 보내")
    class ModifyUmbrella {
        @DisplayName("우산 정보를 수정할 수 있다.")
        @Test
        void sucess() throws Exception {

            // given
            long id = FixtureBuilderFactory.buildLong(1000);
            UmbrellaModifyRequest umbrellaModifyRequest = FixtureBuilderFactory.builderUmbrellaModifyRequest()
                    .sample();

            doNothing().when(umbrellaService).modifyUmbrella(eq(id), refEq(umbrellaModifyRequest));
            // when & then
            mockMvc.perform(
                            patch("/admin/umbrellas/{umbrellaId}", id)
                                    .content(objectMapper.writeValueAsString(umbrellaModifyRequest))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("modify-umbrella-doc",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestFields(
                                    fieldWithPath("uuid").type(JsonFieldType.NUMBER)
                                            .description("우산 관리번호"),
                                    fieldWithPath("storeMetaId").type(JsonFieldType.NUMBER)
                                            .description("지점 고유번호"),
                                    fieldWithPath("rentable").type(JsonFieldType.BOOLEAN)
                                            .description("대여 가능 여부"),
                                    fieldWithPath("missed").type(JsonFieldType.BOOLEAN)
                                            .description("분실 여부"),
                                    fieldWithPath("etc").type(JsonFieldType.STRING)
                                            .optional()
                                            .description("기타 특이 사항")),
                            pathParameters(
                                    parameterWithName("umbrellaId").description("우산 고유번호")
                            )));
        }

        @DisplayName("변경하려는 우산 관리번호가 이미 존재하면 400 에러가 반환된다.")
        @Test
        void existingUmbrellaUuid() throws Exception {

            // given
            long id = FixtureBuilderFactory.buildLong( 1000);
            UmbrellaModifyRequest umbrellaModifyRequest = FixtureBuilderFactory.builderUmbrellaModifyRequest()
                    .sample();

            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UmbrellaExceptionHandler());

            doThrow(new ExistingUmbrellaUuidException("[ERROR] 이미 존재하는 우산 관리번호입니다.")).when(umbrellaService).modifyUmbrella((eq(id)), refEq(umbrellaModifyRequest));

            // when & then
            mockMvc.perform(
                            patch("/admin/umbrellas/{umbrellaId}", id)
                                    .content(objectMapper.writeValueAsString(umbrellaModifyRequest))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(ExistingUmbrellaUuidException.class));
        }

        @DisplayName("변경하려는 우산 고유번호가 존재하지 않으면 400 에러가 반환된다.")
        @Test
        void notExistingUmbrellaId() throws Exception {

            // given
            long id = FixtureBuilderFactory.buildLong(1000);
            UmbrellaModifyRequest umbrellaModifyRequest = FixtureBuilderFactory.builderUmbrellaModifyRequest()
                    .sample();

            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UmbrellaExceptionHandler());

            doThrow(new NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산 관리번호입니다.")).when(umbrellaService).modifyUmbrella((eq(id)), refEq(umbrellaModifyRequest));
            // when & then
            mockMvc.perform(
                            patch("/admin/umbrellas/{umbrellaId}", id)
                                    .content(objectMapper.writeValueAsString(umbrellaModifyRequest))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(NonExistingUmbrellaException.class));
        }
    }

    @Nested
    @DisplayName("사용자는 우산의 고유번호로 DELETE 요청을 보내")
    class DeleteUmbrella {
        @DisplayName("우산 정보를 삭제할 수 있다.")
        @Test
        void success() throws Exception {

            // given
            long id = FixtureBuilderFactory.buildLong(1000);
            willDoNothing().given(umbrellaService)
                    .deleteUmbrella(eq(id));

            // when & then
            mockMvc.perform(
                            delete("/admin/umbrellas/{umbrellaId}", id)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("delete-umbrella-doc",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            pathParameters(
                                    parameterWithName("umbrellaId").description("우산 고유번호")
                            )));
        }

        @DisplayName("존재하지 않는 우산 고유번호면 400 에러를 반환한다.")
        @Test
        void notExistingUmbrella() throws Exception {

            // given
            long id = FixtureBuilderFactory.buildLong(1000);
            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UmbrellaExceptionHandler());

            willThrow(new NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산 고유번호입니다."))
                    .given(umbrellaService).deleteUmbrella(eq(id));

            // when & then
            mockMvc.perform(
                            delete("/admin/umbrellas/{umbrellaId}", id))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(NonExistingUmbrellaException.class));
        }
    }

    @DisplayName("사용자는 전체 우산 통계를 조회할 수 있다.")
    @Test
    void showAllUmbrellasStatisticsTest() throws Exception {

        // given

        UmbrellaStatisticsResponse umbrellaStatisticsResponse = FixtureBuilderFactory
                .builderUmbrellaStatisticsResponse()
                .sample();
        given(umbrellaService.getUmbrellaAllStatistics())
                .willReturn(umbrellaStatisticsResponse);


        // when & then
        mockMvc.perform(
                        get("/admin/umbrellas/statistics")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("show-all-umbrellas-statistics-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                beneathPath("data").withSubsectionId("data"),
                                fieldWithPath("totalRentCount").type(JsonFieldType.NUMBER)
                                        .description("전체 대여 건수"),
                                fieldWithPath("totalUmbrellaCount").type(JsonFieldType.NUMBER)
                                        .description("전체 우산 개수"),
                                fieldWithPath("rentableUmbrellaCount").type(JsonFieldType.NUMBER)
                                        .description("대여 가능 우산 개수"),
                                fieldWithPath("rentedUmbrellaCount").type(JsonFieldType.NUMBER)
                                        .description("대여 중 우산 개수"),
                                fieldWithPath("missingUmbrellaCount").type(JsonFieldType.NUMBER)
                                        .description("분실 우산 개수"),
                                fieldWithPath("missingRate").type(JsonFieldType.NUMBER)
                                        .description("분실률(%)")
                        )));
    }

    @Nested
    @DisplayName("사용자는 협업 지점 고유 번호로 GET 요청을 보내")
    class showUmbrellasStatisticsByStoreIdTest {

        @DisplayName("사용자는 지점 우산 통계를 조회할 수 있다.")
        @Test
        void success() throws Exception {

            // given

            UmbrellaStatisticsResponse umbrellaStatisticsResponse = FixtureBuilderFactory
                    .builderUmbrellaStatisticsResponse()
                    .sample();
            int storeId = FixtureBuilderFactory.buildInteger(100);
            given(umbrellaService.getUmbrellaStatisticsByStoreId(storeId))
                    .willReturn(umbrellaStatisticsResponse);

            // when & then
            mockMvc.perform(
                            get("/admin/umbrellas/statistics/{storeId}", storeId)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("show-umbrellas-statistics-by-store-doc",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            responseFields(
                                    beneathPath("data").withSubsectionId("data"),
                                    fieldWithPath("totalRentCount").type(JsonFieldType.NUMBER)
                                            .description("지점 전체 대여 건수"),
                                    fieldWithPath("totalUmbrellaCount").type(JsonFieldType.NUMBER)
                                            .description("지점 전체 우산 개수"),
                                    fieldWithPath("rentableUmbrellaCount").type(JsonFieldType.NUMBER)
                                            .description("지점 대여 가능 우산 개수"),
                                    fieldWithPath("rentedUmbrellaCount").type(JsonFieldType.NUMBER)
                                            .description("지점 대여 중 우산 개수"),
                                    fieldWithPath("missingUmbrellaCount").type(JsonFieldType.NUMBER)
                                            .description("지점 분실 우산 개수"),
                                    fieldWithPath("missingRate").type(JsonFieldType.NUMBER)
                                            .description("지점 분실률(%)")
                            )));
        }

        @DisplayName("존재하지 않는 협업지점 고유번호면 400 에러를 반환한다.")
        @Test
        void notExistingStoreMeta() throws Exception {

            // given
            long storeId = FixtureBuilderFactory.buildLong(1000);
            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new StoreExceptionHandler());

            willThrow(new NonExistingStoreMetaException("[ERROR] 존재하지 않는 협업지점 고유번호입니다."))
                    .given(umbrellaService).getUmbrellaStatisticsByStoreId(eq(storeId));

            // when & then
            mockMvc.perform(
                            get("/admin/umbrellas/statistics/{storeId}", storeId))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(NonExistingStoreMetaException.class));
        }
    }
}
