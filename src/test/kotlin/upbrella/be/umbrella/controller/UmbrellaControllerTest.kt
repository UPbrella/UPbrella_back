package upbrella.be.umbrella.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.refEq
import org.mockito.BDDMockito.*
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import upbrella.be.config.FixtureBuilderFactory
import upbrella.be.config.FixtureBuilderFactory.buildLong
import upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest
import upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse
import upbrella.be.docs.utils.RestDocsSupport
import upbrella.be.store.controller.StoreExceptionHandler
import upbrella.be.store.exception.NonExistingStoreMetaException
import upbrella.be.umbrella.dto.response.UmbrellaResponse
import upbrella.be.umbrella.exception.ExistingUmbrellaUuidException
import upbrella.be.umbrella.exception.NonExistingUmbrellaException
import upbrella.be.umbrella.service.UmbrellaService

@ExtendWith(MockitoExtension::class)
class UmbrellaControllerTest : RestDocsSupport() {

    @Mock
    private lateinit var umbrellaService: UmbrellaService

    override fun initController(): Any =
        UmbrellaController(umbrellaService)

    @DisplayName("사용자는 전체 우산 현황을 조회할 수 있다.")
    @Test
    fun showAllUmbrellasTest() {
        // given
        val umbrellaResponseList = mutableListOf<UmbrellaResponse>()
        repeat(5) {
            umbrellaResponseList.add(
                FixtureBuilderFactory.builderUmbrellaResponses().sample()
            )
        }

        val pageable: Pageable = PageRequest.of(0, 5)
        given(umbrellaService.findAllUmbrellas(pageable))
            .willReturn(umbrellaResponseList)

        val info: MultiValueMap<String, String> = LinkedMultiValueMap()
        info.add("page", "0")
        info.add("size", "5")

        // when & then
        mockMvc.perform(
            get("/admin/umbrellas")
                .params(info)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "show-all-umbrellas-doc",
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
                    )
                )
            )
    }

    @DisplayName("사용자는 지점 우산 현황을 조회할 수 있다.")
    @Test
    fun showUmbrellasByStoreIdTest() {
        // given
        val storeId = buildLong(100)
        val umbrellaResponseList = mutableListOf<UmbrellaResponse>()

        repeat(7) {
            umbrellaResponseList.add(
                FixtureBuilderFactory.builderUmbrellaResponses()
                    .set("storeMetaId", storeId)
                    .sample()
            )
        }

        val pageable = PageRequest.of(0, 5)
        given(umbrellaService.findUmbrellasByStoreId(storeId.toLong(), pageable))
            .willReturn(umbrellaResponseList)

        val info: MultiValueMap<String, String> = LinkedMultiValueMap()
        info.add("page", "0")
        info.add("size", "5")

        // when & then
        mockMvc.perform(
            RestDocumentationRequestBuilders
                .get("/admin/umbrellas/{storeId}", storeId)
                .params(info)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "show-umbrellas-by-store-id-doc",
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
                    )
                )
            )
    }

    @Nested
    @DisplayName("사용자는 우산의 정보로 POST 요청을 보내")
    inner class AddJavaUmbrella {

        @DisplayName("새로운 우산을 추가할 수 있다.")
        @Test
        fun success() {
            // given
            val umbrellaCreateRequest = FixtureBuilderFactory.builderUmbrellaCreateRequest()
                .sample()

            doNothing().`when`(umbrellaService).addUmbrella(refEq(umbrellaCreateRequest) ?: umbrellaCreateRequest)

            // when & then
            mockMvc.perform(
                post("/admin/umbrellas")
                    .content(objectMapper.writeValueAsString(umbrellaCreateRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andDo(
                    document(
                        "add-umbrellas-doc",
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
                                .description("기타 특이 사항")
                        )
                    )
                )
        }

        @DisplayName("우산 관리번호가 이미 존재하면 400 에러가 반환된다.")
        @Test
        fun existingUmbrellaUuid() {
            // given
            val umbrellaCreateRequest = FixtureBuilderFactory.builderUmbrellaCreateRequest()
                .sample()

            // 여기서 컨트롤러에 예외 핸들러를 설정
            mockMvc = RestDocsSupport.setControllerAdvice(initController(),
                UmbrellaExceptionHandler()
            )

            doThrow(
                ExistingUmbrellaUuidException("[ERROR] 이미 존재하는 우산 관리번호입니다.")
            ).`when`(umbrellaService).addUmbrella(refEq(umbrellaCreateRequest) ?: umbrellaCreateRequest)

            // when & then
            mockMvc.perform(
                post("/admin/umbrellas")
                    .content(objectMapper.writeValueAsString(umbrellaCreateRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isBadRequest)
                .andExpect { result ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(ExistingUmbrellaUuidException::class.java)
                }
        }
    }

    @Nested
    @DisplayName("사용자는 우산의 정보로 PATCH 요청을 보내")
    inner class ModifyJavaUmbrella {

        @DisplayName("우산 정보를 수정할 수 있다.")
        @Test
        fun success() {
            // given
            val id = FixtureBuilderFactory.buildLong(1000)
            val umbrellaModifyRequest = FixtureBuilderFactory.builderUmbrellaModifyRequest()
                .sample()

            doNothing().`when`(umbrellaService).modifyUmbrella(eq(id), refEq(umbrellaModifyRequest) ?: umbrellaModifyRequest)

            // when & then
            mockMvc.perform(
                RestDocumentationRequestBuilders
                    .patch("/admin/umbrellas/{umbrellaId}", id)
                    .content(objectMapper.writeValueAsString(umbrellaModifyRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andDo(
                    document(
                        "modify-umbrella-doc",
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
                                .description("기타 특이 사항")
                        ),
                        pathParameters(
                            parameterWithName("umbrellaId").description("우산 고유번호")
                        )
                    )
                )
        }

        @DisplayName("변경하려는 우산 관리번호가 이미 존재하면 400 에러가 반환된다.")
        @Test
        fun existingUmbrellaUuid() {
            // given
            val id = FixtureBuilderFactory.buildLong(1000)
            val umbrellaModifyRequest = FixtureBuilderFactory.builderUmbrellaModifyRequest()
                .sample()

            mockMvc = RestDocsSupport.setControllerAdvice(initController(),
                UmbrellaExceptionHandler()
            )

            doThrow(
                ExistingUmbrellaUuidException("[ERROR] 이미 존재하는 우산 관리번호입니다.")
            ).`when`(umbrellaService).modifyUmbrella(eq(id), refEq(umbrellaModifyRequest) ?: umbrellaModifyRequest)

            // when & then
            mockMvc.perform(
                patch("/admin/umbrellas/{umbrellaId}", id)
                    .content(objectMapper.writeValueAsString(umbrellaModifyRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isBadRequest)
                .andExpect { result ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(ExistingUmbrellaUuidException::class.java)
                }
        }

        @DisplayName("변경하려는 우산 고유번호가 존재하지 않으면 400 에러가 반환된다.")
        @Test
        fun notExistingUmbrellaId() {
            // given
            val id = FixtureBuilderFactory.buildLong(1000)
            val umbrellaModifyRequest = FixtureBuilderFactory.builderUmbrellaModifyRequest()
                .sample()

            mockMvc = RestDocsSupport.setControllerAdvice(initController(),
                UmbrellaExceptionHandler()
            )

            doThrow(
                NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산 관리번호입니다.")
            ).`when`(umbrellaService).modifyUmbrella(eq(id), refEq(umbrellaModifyRequest) ?: umbrellaModifyRequest)

            // when & then
            mockMvc.perform(
                patch("/admin/umbrellas/{umbrellaId}", id)
                    .content(objectMapper.writeValueAsString(umbrellaModifyRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isBadRequest)
                .andExpect { result ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(NonExistingUmbrellaException::class.java)
                }
        }
    }

    @Nested
    @DisplayName("사용자는 우산의 고유번호로 DELETE 요청을 보내")
    inner class DeleteJavaUmbrella {

        @DisplayName("우산 정보를 삭제할 수 있다.")
        @Test
        fun success() {
            // given
            val id = FixtureBuilderFactory.buildLong(1000)
            willDoNothing().given(umbrellaService).deleteUmbrella(eq(id))

            // when & then
            mockMvc.perform(
                RestDocumentationRequestBuilders
                    .delete("/admin/umbrellas/{umbrellaId}", id)
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andDo(
                    document(
                        "delete-umbrella-doc",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                            parameterWithName("umbrellaId").description("우산 고유번호")
                        )
                    )
                )
        }

        @DisplayName("존재하지 않는 우산 고유번호면 400 에러를 반환한다.")
        @Test
        fun notExistingUmbrella() {
            // given
            val id = FixtureBuilderFactory.buildLong(1000)
            mockMvc = RestDocsSupport.setControllerAdvice(initController(),
                UmbrellaExceptionHandler()
            )

            willThrow(
                NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산 고유번호입니다.")
            ).given(umbrellaService).deleteUmbrella(eq(id))

            // when & then
            mockMvc.perform(
                delete("/admin/umbrellas/{umbrellaId}", id)
            )
                .andExpect(status().isBadRequest)
                .andExpect { result ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(NonExistingUmbrellaException::class.java)
                }
        }
    }

    @DisplayName("사용자는 전체 우산 통계를 조회할 수 있다.")
    @Test
    fun showAllUmbrellasStatisticsTest() {
        // given
        val umbrellaStatisticsResponse = FixtureBuilderFactory
            .builderUmbrellaStatisticsResponse()
            .sample()

        given(umbrellaService.getUmbrellaAllStatistics())
            .willReturn(umbrellaStatisticsResponse)

        // when & then
        mockMvc.perform(
            get("/admin/umbrellas/statistics")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "show-all-umbrellas-statistics-doc",
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
                    )
                )
            )
    }

    @Nested
    @DisplayName("사용자는 협업 지점 고유 번호로 GET 요청을 보내")
    inner class ShowUmbrellasStatisticsByStoreIdTest {

        @DisplayName("사용자는 지점 우산 통계를 조회할 수 있다.")
        @Test
        fun success() {
            // given
            val umbrellaStatisticsResponse = FixtureBuilderFactory
                .builderUmbrellaStatisticsResponse()
                .sample()

            val storeId = FixtureBuilderFactory.buildInteger(100)
            given(umbrellaService.getUmbrellaStatisticsByStoreId(storeId.toLong()))
                .willReturn(umbrellaStatisticsResponse)

            // when & then
            mockMvc.perform(
                get("/admin/umbrellas/statistics/{storeId}", storeId)
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andDo(
                    document(
                        "show-umbrellas-statistics-by-store-doc",
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
                        )
                    )
                )
        }

        @DisplayName("존재하지 않는 협업지점 고유번호면 400 에러를 반환한다.")
        @Test
        fun notExistingStoreMeta() {
            // given
            val storeId = FixtureBuilderFactory.buildLong(1000)
            mockMvc = setControllerAdvice(initController(), StoreExceptionHandler())

            willThrow(
                NonExistingStoreMetaException("[ERROR] 존재하지 않는 협업지점 고유번호입니다.")
            ).given(umbrellaService).getUmbrellaStatisticsByStoreId(eq(storeId))

            // when & then
            mockMvc.perform(
                get("/admin/umbrellas/statistics/{storeId}", storeId)
            )
                .andExpect(status().isBadRequest)
                .andExpect { result ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(NonExistingStoreMetaException::class.java)
                }
        }
    }
}
