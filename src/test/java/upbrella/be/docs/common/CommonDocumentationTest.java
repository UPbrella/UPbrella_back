package upbrella.be.docs.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.test.web.servlet.ResultActions;
import upbrella.be.docs.utils.CustomResponseFieldsSnippet;
import upbrella.be.docs.utils.RestDocsSupport;

import java.util.Arrays;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static upbrella.be.docs.utils.ApiDocumentUtils.getDocumentResponse;

public class CommonDocumentationTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new CustomController();
    }


    public static CustomResponseFieldsSnippet customResponseFields(String type,
                                                                   PayloadSubsectionExtractor<?> subsectionExtractor,
                                                                   Map<String, Object> attributes, FieldDescriptor... descriptors) {
        return new CustomResponseFieldsSnippet(type, subsectionExtractor, Arrays.asList(descriptors), attributes
                , true);
    }

    @DisplayName("모든 응답은 응답 코드, 데이터, 메시지를 포함한다.")
    @Test
    public void commons() throws Exception {
        ResultActions result = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/docs/common")
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andDo(document("common-docs",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        customResponseFields("custom-response", null, null,
                                subsectionWithPath("data").description("데이터"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태")
                                )
                ));

    }

    @DisplayName("오류가 발생한 응답은 응답 코드, 데이터, 메시지를 포함한다.")
    @Test
    public void errorCommons() throws Exception {
        ResultActions result = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/docs/error")
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andDo(document("error-docs",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        customResponseFields("custom-response", null, null,
                                subsectionWithPath("data").description("데이터"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("상태")
                        )
                ));

    }

}
