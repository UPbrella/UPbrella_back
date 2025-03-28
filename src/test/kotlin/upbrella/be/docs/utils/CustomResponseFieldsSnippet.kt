package upbrella.be.docs.utils

import org.springframework.http.MediaType
import org.springframework.restdocs.operation.Operation
import org.springframework.restdocs.payload.AbstractFieldsSnippet
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadSubsectionExtractor
import java.io.IOException

class CustomResponseFieldsSnippet(
    type: String,
    subsectionExtractor: PayloadSubsectionExtractor<*>?,
    descriptors: List<FieldDescriptor>,
    attributes: Map<String, Any>?,
    ignoreUndocumentedFields: Boolean
) : AbstractFieldsSnippet(
    type,
    descriptors,
    attributes,
    ignoreUndocumentedFields,
    subsectionExtractor
) {

    override fun getContentType(operation: Operation): MediaType? {
        return operation.response.headers.contentType
    }

    @Throws(IOException::class)
    override fun getContent(operation: Operation): ByteArray {
        return operation.response.content
    }
}