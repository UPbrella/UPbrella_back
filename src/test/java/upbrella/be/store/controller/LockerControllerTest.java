package upbrella.be.store.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.rent.dto.response.LockerPasswordResponse;
import upbrella.be.rent.service.LockerService;
import upbrella.be.store.dto.request.CreateLockerRequest;
import upbrella.be.store.dto.request.UpdateLockerCountRequest;
import upbrella.be.store.dto.request.UpdateLockerRequest;
import upbrella.be.store.dto.response.AllLockerResponse;
import upbrella.be.store.dto.response.SingleLockerResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse;

@ExtendWith(MockitoExtension.class)
class LockerControllerTest extends RestDocsSupport {

    @Mock
    private LockerService lockerService;

    @Test
    @DisplayName("모든 보관함을 조회할 수 있다.")
    void findAllLockerTest() throws Exception {
        // given
        SingleLockerResponse locker = SingleLockerResponse.builder()
                .id(1L)
                .storeMetaId(1L)
                .secretKey("secretKey")
                .build();

        AllLockerResponse response = AllLockerResponse.builder()
                .lockers(List.of(locker))
                .build();

        given(lockerService.findAll()).willReturn(response);

        // when & then
        mockMvc.perform(
                        get("/admin/lockers")
                ).andDo(print())
                .andExpect(status().isOk())
                .andDo(document("find-all-lockers",
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
                        )));
    }

    @Test
    @DisplayName("새로운 보관함을 생성할 수 있다.")
    void createLockerTest() throws Exception {
        // given
        CreateLockerRequest request = CreateLockerRequest.builder()
                .storeId(1L)
                .secretKey("12345678901234567890123456789012")
                .build();

        // then
        mockMvc.perform(post("/admin/lockers")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("create-locker",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("storeId").type(JsonFieldType.NUMBER)
                                        .description("보관함이 속할 매장 ID"),
                                fieldWithPath("secretKey").type(JsonFieldType.STRING)
                                        .description("보관함 비밀키")
                        )));
    }

    @Test
    @DisplayName("보관함의 정보를 수정할 수 있다.")
    void updateLockerTest() throws Exception {
        // given
        UpdateLockerRequest request = UpdateLockerRequest.builder()
                .storeId(1L)
                .secretKey("12345678901234567890123456789012")
                .build();
        Long lockerId = 1L;

        // when

        // then
        mockMvc.perform(patch("/admin/lockers/{lockerId}", lockerId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update-locker",
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
                        )));
    }

    @Test
    @DisplayName("보관함을 삭제할 수 있다.")
    void deleteLockerTest() throws Exception {
        // given
        Long lockerId = 1L;

        // then
        mockMvc.perform(delete("/admin/lockers/{lockerId}", lockerId))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete-locker",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("lockerId").description("보관함 ID")
                        )));
    }

    @Test
    @DisplayName("보관함의 카운트를 동기화할 수 있다.")
    void updateLockerCount() throws Exception {
        // given
        Long storeId = 1L;
        UpdateLockerCountRequest request = UpdateLockerCountRequest.builder()
                .count(1L)
                .build();

        LockerPasswordResponse response = new LockerPasswordResponse("1234");
        given(lockerService.updateCount(anyLong(), any())).willReturn(response);

        // when & then
        mockMvc.perform(patch("/lockers/{storeMetaId}", storeId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("update-locker-count",
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
                        )));
    }


    @Override
    protected Object initController() {
        return new LockerController(lockerService);
    }
}
