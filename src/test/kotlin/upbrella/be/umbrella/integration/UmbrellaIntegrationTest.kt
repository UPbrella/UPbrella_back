package upbrella.be.umbrella.integration

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import upbrella.be.config.FixtureBuilderFactory
import upbrella.be.docs.utils.RestDocsSupport
import upbrella.be.store.entity.ClassificationType
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.controller.UmbrellaController
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.umbrella.service.UmbrellaService
import javax.persistence.EntityManager

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UmbrellaIntegrationTest : RestDocsSupport() {

    @Autowired
    private lateinit var umbrellaService: UmbrellaService

    @Autowired
    private lateinit var em: EntityManager

    private lateinit var storeMeta: StoreMeta
    private lateinit var umbrellaList: MutableList<Umbrella>

    override fun initController(): Any {
        return UmbrellaController(umbrellaService)
    }

    @BeforeEach
    fun setUp() {
        val classification = FixtureBuilderFactory.builderClassification()
            .set("type", ClassificationType.CLASSIFICATION)
            .set("id", null)
            .sample()

        val subClassification = FixtureBuilderFactory.builderClassification()
            .set("type", ClassificationType.SUB_CLASSIFICATION)
            .set("id", null)
            .sample()

        em.persist(classification)
        em.persist(subClassification)
        em.flush()

        storeMeta = FixtureBuilderFactory.builderStoreMeta()
            .set("id", null)
            .set("classification", classification)
            .set("subClassification", subClassification)
            .set("businessHours", null)
            .sample()

        em.persist(storeMeta)
        em.flush()

        // 대여 가능 우산 3개 생성
        umbrellaList = ArrayList()
        repeat(3) { i ->
            val umbrella = FixtureBuilderFactory.builderUmbrella()
                .set("id", null)
                .set("storeMeta", storeMeta)
                .set("rentable", true)
                .set("deleted", false)
                .set("missed", false)
                .set("uuid", 1L + i) // uuid를 (1,2,3...)로 설정
                .sample()

            umbrellaList.add(umbrella)
            em.persist(umbrella)
            em.flush()
        }

        // 대여 중 우산 2개 생성
        repeat(2) { i ->
            val umbrella = FixtureBuilderFactory.builderUmbrella()
                .set("id", null)
                .set("storeMeta", storeMeta)
                .set("rentable", false)
                .set("deleted", false)
                .set("missed", false)
                .set("uuid", 100L + i) // uuid를 100번대로 설정
                .sample()

            umbrellaList.add(umbrella)
            em.persist(umbrella)
            em.flush()
        }

        // 분실 우산 1개 생성
        repeat(1) {
            val umbrella = FixtureBuilderFactory.builderUmbrella()
                .set("id", null)
                .set("storeMeta", storeMeta)
                .set("rentable", false)
                .set("deleted", false)
                .set("missed", true)
                .set("uuid", 200L) // uuid를 200으로 설정
                .sample()

            umbrellaList.add(umbrella)
            em.persist(umbrella)
            em.flush()
        }
    }

    @Test
    @DisplayName("관리자는 우산을 등록할 수 있다.")
    fun addUmbrellaTest() {
        // given
        val umbrellaCreateRequest = FixtureBuilderFactory.builderUmbrellaCreateRequest()
            .set("id", 999L)
            .set("storeMetaId", storeMeta.id)
            .set("rentable", true)
            .set("uuid", 3000L)
            .sample()

        // when & then
        mockMvc.perform(
            post("/admin/umbrellas")
                .content(objectMapper.writeValueAsString(umbrellaCreateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/admin/umbrellas")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.data.umbrellaResponsePage").exists())
            .andExpect(jsonPath("\$.data.umbrellaResponsePage.length()").value(umbrellaList.size + 1))
            .andExpect(jsonPath("\$.data.umbrellaResponsePage[6].storeMetaId").value(umbrellaCreateRequest.storeMetaId))
            .andExpect(jsonPath("\$.data.umbrellaResponsePage[6].uuid").value(umbrellaCreateRequest.uuid))
            .andExpect(jsonPath("\$.data.umbrellaResponsePage[6].rentable").value(umbrellaCreateRequest.rentable))
    }

    @Test
    @DisplayName("사용자는 전체 우산 현황을 조회할 수 있다.")
    fun showAllUmbrellasTest() {
        // when & then
        mockMvc.perform(
            get("/admin/umbrellas")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.data.umbrellaResponsePage").exists())
            .andExpect(jsonPath("\$.data.umbrellaResponsePage.length()").value(6))
            .andExpect(jsonPath("\$.data.umbrellaResponsePage[0].storeMetaId").value(storeMeta.id))
            .andExpect(jsonPath("\$.data.umbrellaResponsePage[0].uuid").value(umbrellaList[0].uuid))
            .andExpect(jsonPath("\$.data.umbrellaResponsePage[0].rentable").value(umbrellaList[0].rentable))
    }

    @Test
    @DisplayName("사용자는 지점 우산 현황을 조회할 수 있다.")
    fun showUmbrellasByStoreIdTest() {
        // when & then
        mockMvc.perform(
            get("/admin/umbrellas/{storeId}", storeMeta.id)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.data.umbrellaResponsePage").exists())
            .andExpect(jsonPath("\$.data.umbrellaResponsePage.length()").value(6))
            .andExpect(jsonPath("\$.data.umbrellaResponsePage[0].storeMetaId").value(storeMeta.id))
            .andExpect(jsonPath("\$.data.umbrellaResponsePage[0].uuid").value(umbrellaList[0].uuid))
            .andExpect(jsonPath("\$.data.umbrellaResponsePage[0].rentable").value(umbrellaList[0].rentable))
    }

    @Test
    @DisplayName("사용자는 우산 정보를 수정할 수 있다.")
    fun modifyUmbrellaTest() {
        // given
        val id = umbrellaList[0].id
        val umbrellaModifyRequest = FixtureBuilderFactory.builderUmbrellaModifyRequest()
            .set("storeMetaId", storeMeta.id)
            .set("rentable", true)
            .set("uuid", 1L)
            .sample()

        // when & then
        mockMvc.perform(
            patch("/admin/umbrellas/{umbrellaId}", id)
                .content(objectMapper.writeValueAsString(umbrellaModifyRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/admin/umbrellas")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.data.umbrellaResponsePage").exists())
            .andExpect(jsonPath("\$.data.umbrellaResponsePage.length()").value(umbrellaList.size))
            .andExpect(jsonPath("\$.data.umbrellaResponsePage[0].storeMetaId").value(umbrellaModifyRequest.storeMetaId))
            .andExpect(jsonPath("\$.data.umbrellaResponsePage[0].uuid").value(umbrellaModifyRequest.uuid))
            .andExpect(jsonPath("\$.data.umbrellaResponsePage[0].rentable").value(umbrellaModifyRequest.rentable))
    }

    @Test
    @DisplayName("관리자는 우산 정보를 삭제할 수 있다.")
    fun deleteUmbrellaTest() {
        // given
        val id = umbrellaList[0].id

        // when & then
        mockMvc.perform(
            delete("/admin/umbrellas/{umbrellaId}", id)
        )
            .andDo(print())
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/admin/umbrellas")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.data.umbrellaResponsePage").exists())
            .andExpect(jsonPath("\$.data.umbrellaResponsePage.length()").value(umbrellaList.size - 1))
    }

    @Test
    @DisplayName("관리자는 전체 우산 통계를 조회할 수 있다.")
    fun showAllUmbrellasStatisticsTest() {
        // when & then
        mockMvc.perform(
            get("/admin/umbrellas/statistics")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.data").exists())
            .andExpect(jsonPath("\$.data.totalUmbrellaCount").value(6))
            .andExpect(jsonPath("\$.data.rentableUmbrellaCount").value(3))
            .andExpect(jsonPath("\$.data.rentedUmbrellaCount").value(2))
            .andExpect(jsonPath("\$.data.missingUmbrellaCount").value(1))
            .andExpect(jsonPath("\$.data.missingRate").value(16))
            .andExpect(jsonPath("\$.data.totalRentCount").value(0))
    }

    @Test
    @DisplayName("관리자는 지점 우산 통계를 조회할 수 있다.")
    fun success() {
        // when & then
        mockMvc.perform(
            get("/admin/umbrellas/statistics/{storeId}", storeMeta.id)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.data").exists())
            .andExpect(jsonPath("\$.data.totalUmbrellaCount").value(6))
            .andExpect(jsonPath("\$.data.rentableUmbrellaCount").value(3))
            .andExpect(jsonPath("\$.data.rentedUmbrellaCount").value(2))
            .andExpect(jsonPath("\$.data.missingUmbrellaCount").value(1))
            .andExpect(jsonPath("\$.data.missingRate").value(16))
            .andExpect(jsonPath("\$.data.totalRentCount").value(0))
    }
}
