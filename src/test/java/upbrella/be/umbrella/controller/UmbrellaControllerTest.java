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
import upbrella.be.umbrella.dto.request.UmbrellaRequest;
import upbrella.be.umbrella.dto.response.UmbrellaResponse;
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
                        get("/umbrellas")
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
                                fieldWithPath("umbrellaResponsePage[].storeMetaId").type(JsonFieldType.NUMBER)
                                        .description("보관 지점 고유번호"),
                                fieldWithPath("umbrellaResponsePage[].uuid").type(JsonFieldType.NUMBER)
                                        .description("우산 관리번호"),
                                fieldWithPath("umbrellaResponsePage[].rentable").type(JsonFieldType.BOOLEAN)
                                        .description("대여 가능 상태")
                        )));
    }

    @DisplayName("사용자는 지점 우산 현황을 조회할 수 있다.")
    @Test
    void showUmbrellasByStoreIdTest() throws Exception {

        // given
        int storeId = FixtureBuilderFactory.buildInteger();
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
                        get("/umbrellas/{storeId}", storeId)
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
                                fieldWithPath("umbrellaResponsePage[].storeMetaId").type(JsonFieldType.NUMBER)
                                        .description("보관 지점번호"),
                                fieldWithPath("umbrellaResponsePage[].uuid").type(JsonFieldType.NUMBER)
                                        .description("우산 관리번호"),
                                fieldWithPath("umbrellaResponsePage[].rentable").type(JsonFieldType.BOOLEAN)
                                        .description("대여 가능 상태")
                        )));
    }

    @Nested
    @DisplayName("사용자는 우산의 정보로 POST 요청을 보내")
    class AddUmbrella {

        @DisplayName("새로운 우산을 추가할 수 있다.")
        @Test
        void success() throws Exception {

            // given
            UmbrellaRequest umbrellaRequest = FixtureBuilderFactory.builderUmbrellaRequest()
                    .sample();
            given(umbrellaService.addUmbrella(refEq(umbrellaRequest)))
                    .willReturn(FixtureBuilderFactory.builderUmbrella().sample());

            // when & then
            mockMvc.perform(
                            post("/umbrellas")
                                    .content(objectMapper.writeValueAsString(umbrellaRequest))
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
            UmbrellaRequest umbrellaRequest = FixtureBuilderFactory.builderUmbrellaRequest()
                    .sample();

            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UmbrellaExceptionHandler());

            given(umbrellaService.addUmbrella(refEq(umbrellaRequest)))
                    .willThrow(new ExistingUmbrellaUuidException("[ERROR] 이미 존재하는 우산 관리번호입니다."));

            // when & then
            mockMvc.perform(
                            post("/umbrellas")
                                    .content(objectMapper.writeValueAsString(umbrellaRequest))
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
            long id = FixtureBuilderFactory.buildLong();
            UmbrellaRequest umbrellaRequest = FixtureBuilderFactory.builderUmbrellaRequest()
                    .sample();

            given(umbrellaService.modifyUmbrella(eq(id), refEq(umbrellaRequest)))
                    .willReturn(FixtureBuilderFactory.builderUmbrella().sample());

            // when & then
            mockMvc.perform(
                            patch("/umbrellas/{umbrellaId}", id)
                                    .content(objectMapper.writeValueAsString(umbrellaRequest))
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
            long id = FixtureBuilderFactory.buildLong();
            UmbrellaRequest umbrellaRequest = FixtureBuilderFactory.builderUmbrellaRequest()
                    .sample();

            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UmbrellaExceptionHandler());

            given(umbrellaService.modifyUmbrella(eq(id), refEq(umbrellaRequest)))
                    .willThrow(new ExistingUmbrellaUuidException("[ERROR] 이미 존재하는 우산 관리번호입니다."));

            // when & then
            mockMvc.perform(
                            patch("/umbrellas/{umbrellaId}", id)
                                    .content(objectMapper.writeValueAsString(umbrellaRequest))
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
            long id = FixtureBuilderFactory.buildLong();
            UmbrellaRequest umbrellaRequest = FixtureBuilderFactory.builderUmbrellaRequest()
                    .sample();

            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UmbrellaExceptionHandler());

            given(umbrellaService.modifyUmbrella(eq(id), refEq(umbrellaRequest)))
                    .willThrow(new NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산 관리번호입니다."));

            // when & then
            mockMvc.perform(
                            patch("/umbrellas/{umbrellaId}", id)
                                    .content(objectMapper.writeValueAsString(umbrellaRequest))
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
            long id = FixtureBuilderFactory.buildLong();
            willDoNothing().given(umbrellaService)
                    .deleteUmbrella(eq(id));

            // when & then
            mockMvc.perform(
                            delete("/umbrellas/{umbrellaId}", id)
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
            long id = FixtureBuilderFactory.buildLong();
            mockMvc = RestDocsSupport.setControllerAdvice(initController(), new UmbrellaExceptionHandler());

            willThrow(new NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산 고유번호입니다."))
                    .given(umbrellaService).deleteUmbrella(eq(id));

            // when & then
            mockMvc.perform(
                            delete("/umbrellas/{umbrellaId}", id))
                    .andExpect(status().isBadRequest())
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(NonExistingUmbrellaException.class));
        }
    }
}
