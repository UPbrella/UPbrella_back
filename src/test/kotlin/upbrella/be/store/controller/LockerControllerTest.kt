package upbrella.be.store.controller

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest
import upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse
import upbrella.be.docs.utils.RestDocsSupport
import upbrella.be.rent.dto.response.LockerPasswordResponse
import upbrella.be.rent.service.LockerService
import upbrella.be.store.dto.request.CreateLockerRequest
import upbrella.be.store.dto.request.UpdateLockerCountRequest
import upbrella.be.store.dto.request.UpdateLockerRequest
import upbrella.be.store.dto.response.AllLockerResponse
import upbrella.be.store.dto.response.SingleLockerResponse

@ExtendWith(MockitoExtension::class)
class LockerControllerTest : RestDocsSupport() {

    @Mock
    private lateinit var lockerService: LockerService

    override fun initController(): Any {
        return LockerController(lockerService)
    }

    @Test
    @DisplayName("모든 보관함을 조회할 수 있다.")
    fun findAllLockerTest() {
        // given
        val locker = SingleLockerResponse.builder()
            .id(1L)
            .storeMetaId(1L)
            .secretKey("secretKey")
            .build()

        val response = AllLockerResponse.builder()
            .lockers(listOf(locker))
            .build()

        given(lockerService.findAll()).willReturn(response)

        // when & then
        mockMvc.perform(
            get("/admin/lockers")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "find-all-lockers",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        fieldWithPath("lockers").type(JsonFieldType.ARRAY)
                            .description("보관함 목록"),
                        fieldWithPath("lockers[].id").type(JsonFieldType.NUMBER)
                            .description("보관함 ID"),
                        fieldWithPath("lockers[].storeMetaId").type(JsonFieldType.NUMBER)
                            .description("보관함이 속한 매장 ID"),
                        fieldWithPath("lockers[].secretKey").type(JsonFieldType.STRING)
                            .description("보관함 비밀키")
                    )
                )
            )
    }

    @Test
    @DisplayName("새로운 보관함을 생성할 수 있다.")
    fun createLockerTest() {
        // given
        val request = CreateLockerRequest.builder()
            .storeId(1L)
            .secretKey("12345678901234567890123456789012")
            .build()

        // then
        mockMvc.perform(
            post("/admin/lockers")
                .content(objectMapper.writeValueAsString(request))
                .contentType("application/json")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "create-locker",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestFields(
                        fieldWithPath("storeId").type(JsonFieldType.NUMBER)
                            .description("보관함이 속할 매장 ID"),
                        fieldWithPath("secretKey").type(JsonFieldType.STRING)
                            .description("보관함 비밀키")
                    )
                )
            )
    }

    @Test
    @DisplayName("보관함의 정보를 수정할 수 있다.")
    fun updateLockerTest() {
        // given
        val request = UpdateLockerRequest.builder()
            .storeId(1L)
            .secretKey("12345678901234567890123456789012")
            .build()
        val lockerId = 1L

        // when & then
        mockMvc.perform(
            patch("/admin/lockers/{lockerId}", lockerId)
                .content(objectMapper.writeValueAsString(request))
                .contentType("application/json")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "update-locker",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("lockerId").description("보관함 ID")
                    ),
                    requestFields(
                        fieldWithPath("storeId").type(JsonFieldType.NUMBER)
                            .description("보관함이 속할 매장 ID"),
                        fieldWithPath("secretKey").type(JsonFieldType.STRING)
                            .description("보관함 비밀키")
                    )
                )
            )
    }

    @Test
    @DisplayName("보관함을 삭제할 수 있다.")
    fun deleteLockerTest() {
        // given
        val lockerId = 1L

        // then
        mockMvc.perform(
            delete("/admin/lockers/{lockerId}", lockerId)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "delete-locker",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("lockerId").description("보관함 ID")
                    )
                )
            )
    }

    @Test
    @DisplayName("보관함의 카운트를 동기화할 수 있다.")
    fun updateLockerCount() {
        // given
        val storeId = 1L
        val request = UpdateLockerCountRequest.builder()
            .count(1L)
            .build()

        val response = LockerPasswordResponse("1234")
        given(lockerService.updateCount(any<Long>() ?: 0, any<UpdateLockerCountRequest>() ?: request)).willReturn(response)

        // when & then
        mockMvc.perform(
            patch("/lockers/{storeMetaId}", storeId)
                .content(objectMapper.writeValueAsString(request))
                .contentType("application/json")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "update-locker-count",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("storeMetaId").description("협업지점 ID")
                    ),
                    requestFields(
                        fieldWithPath("count").type(JsonFieldType.NUMBER)
                            .description("보관함 카운트")
                    ),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        fieldWithPath("password").type(JsonFieldType.STRING)
                            .description("보관함 비밀번호")
                    )
                )
            )
    }
}
