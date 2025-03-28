package upbrella.be.docs.utils

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.*

object ApiDocumentUtils {
    @JvmStatic
    fun getDocumentRequest(): OperationRequestPreprocessor {
        return preprocessRequest(
            modifyUris()
                .scheme("https")
                .host("api.upbrella.co.kr")
                .removePort(),
            prettyPrint()
        )
    }

    @JvmStatic
    fun getDocumentResponse(): OperationResponsePreprocessor {
        return preprocessResponse(prettyPrint()) // (3)
    }
}