package upbrella.be.docs.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@ExtendWith(RestDocumentationExtension::class)
abstract class RestDocsSupport {

    protected lateinit var mockMvc: MockMvc
    @JvmField
    protected val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerModules(JavaTimeModule())
    }

    @BeforeEach
    fun setup(provider: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(initController())
            .setCustomArgumentResolvers(PageableHandlerMethodArgumentResolver())
            .apply<StandaloneMockMvcBuilder>(documentationConfiguration(provider))
            .build()
        objectMapper.registerModules(JavaTimeModule())
    }

    companion object {
        @JvmStatic
        fun setControllerAdvice(initController: Any, controllerAdvice: Any): MockMvc {
            return MockMvcBuilders.standaloneSetup(initController)
                .setCustomArgumentResolvers(PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(controllerAdvice)
                .build()
        }
    }

    protected abstract fun initController(): Any
}