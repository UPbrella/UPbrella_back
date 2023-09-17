package upbrella.be.umbrella.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.config.FixtureBuilderFactory;
import upbrella.be.docs.utils.RestDocsSupport;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.ClassificationType;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.controller.UmbrellaController;
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest;
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.umbrella.service.UmbrellaService;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UmbrellaIntegrationTest extends RestDocsSupport {

    @Autowired
    private UmbrellaService umbrellaService;
    @Autowired
    private EntityManager em;
    private StoreMeta storeMeta;
    private List<Umbrella> umbrellaList;

    @Override
    protected Object initController() {
        return new UmbrellaController(umbrellaService);
    }

    @BeforeEach
    void setUp() {

        Classification classification = FixtureBuilderFactory.builderClassification()
                .set("type", ClassificationType.CLASSIFICATION)
                .set("id", null).sample();

        Classification subClassification = FixtureBuilderFactory.builderClassification()
                .set("type", ClassificationType.SUB_CLASSIFICATION)
                .set("id", null).sample();

        em.persist(classification);
        em.persist(subClassification);
        em.flush();

        storeMeta = FixtureBuilderFactory.builderStoreMeta()
                .set("id", null)
                .set("classification", classification)
                .set("subClassification", subClassification)
                .set("businessHours", null).sample();

        em.persist(storeMeta);
        em.flush();

        // 대여 가능 우산 3개 생성
        umbrellaList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Umbrella umbrella = FixtureBuilderFactory.builderUmbrella()
                    .set("id", null)
                    .set("storeMeta", storeMeta)
                    .set("rentable", true)
                    .set("deleted", false)
                    .set("missed", false)
                    .sample();

            umbrellaList.add(umbrella);
            em.persist(umbrella);
            em.flush();
        }

        // 대여 중 우산 2개 생성
        for (int i = 0; i < 2; i++) {
            Umbrella umbrella = FixtureBuilderFactory.builderUmbrella()
                    .set("id", null)
                    .set("storeMeta", storeMeta)
                    .set("rentable", false)
                    .set("deleted", false)
                    .set("missed", false)
                    .sample();

            umbrellaList.add(umbrella);
            em.persist(umbrella);
            em.flush();
        }

        // 분실 우산 1개 생성
        for (int i = 0; i < 1; i++) {
            Umbrella umbrella = FixtureBuilderFactory.builderUmbrella()
                    .set("id", null)
                    .set("storeMeta", storeMeta)
                    .set("rentable", false)
                    .set("deleted", false)
                    .set("missed", true)
                    .sample();

            umbrellaList.add(umbrella);
            em.persist(umbrella);
            em.flush();
        }
    }

    @Test
    @DisplayName("관리자는 우산을 등록할 수 있다.")
    void addUmbrellaTest() throws Exception {

        // given
        UmbrellaCreateRequest umbrellaCreateRequest = FixtureBuilderFactory.builderUmbrellaCreateRequest()
                .set("storeMetaId", storeMeta.getId())
                .sample();

        // when & then
        mockMvc.perform(
                        post("/admin/umbrellas")
                                .content(objectMapper.writeValueAsString(umbrellaCreateRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/admin/umbrellas")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.umbrellaResponsePage").exists())
                .andExpect(jsonPath("$.data.umbrellaResponsePage.length()").value(umbrellaList.size() + 1))
                .andExpect(jsonPath("$.data.umbrellaResponsePage[6].storeMetaId").value(umbrellaCreateRequest.getStoreMetaId()))
                .andExpect(jsonPath("$.data.umbrellaResponsePage[6].uuid").value(umbrellaCreateRequest.getUuid()))
                .andExpect(jsonPath("$.data.umbrellaResponsePage[6].rentable").value(umbrellaCreateRequest.isRentable()));
    }


    @Test
    @DisplayName("사용자는 전체 우산 현황을 조회할 수 있다.")
    void showAllUmbrellasTest() throws Exception {

        // when & then
        mockMvc.perform(
                        get("/admin/umbrellas")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.umbrellaResponsePage").exists())
                .andExpect(jsonPath("$.data.umbrellaResponsePage.length()").value(6))
                .andExpect(jsonPath("$.data.umbrellaResponsePage[0].storeMetaId").value(storeMeta.getId()))
                .andExpect(jsonPath("$.data.umbrellaResponsePage[0].uuid").value(umbrellaList.get(0).getUuid()))
                .andExpect(jsonPath("$.data.umbrellaResponsePage[0].rentable").value(umbrellaList.get(0).isRentable()));
    }


    @DisplayName("사용자는 지점 우산 현황을 조회할 수 있다.")
    @Test
    void showUmbrellasByStoreIdTest() throws Exception {

        // when & then
        mockMvc.perform(
                        get("/admin/umbrellas/{storeId}", storeMeta.getId())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.umbrellaResponsePage").exists())
                .andExpect(jsonPath("$.data.umbrellaResponsePage.length()").value(6))
                .andExpect(jsonPath("$.data.umbrellaResponsePage[0].storeMetaId").value(storeMeta.getId()))
                .andExpect(jsonPath("$.data.umbrellaResponsePage[0].uuid").value(umbrellaList.get(0).getUuid()))
                .andExpect(jsonPath("$.data.umbrellaResponsePage[0].rentable").value(umbrellaList.get(0).isRentable()));
        ;
    }


    @DisplayName("사용자는 우산 정보를 수정할 수 있다.")
    @Test
    void modifyUmbrellaTest() throws Exception {

        // given
        long id = umbrellaList.get(0).getId();
        UmbrellaModifyRequest umbrellaModifyRequest = FixtureBuilderFactory.builderUmbrellaModifyRequest()
                .set("storeMetaId", storeMeta.getId())
                .sample();

        // when & then
        mockMvc.perform(
                        patch("/admin/umbrellas/{umbrellaId}", id)
                                .content(objectMapper.writeValueAsString(umbrellaModifyRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/admin/umbrellas")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.umbrellaResponsePage").exists())
                .andExpect(jsonPath("$.data.umbrellaResponsePage.length()").value(umbrellaList.size()))
                .andExpect(jsonPath("$.data.umbrellaResponsePage[0].storeMetaId").value(umbrellaModifyRequest.getStoreMetaId()))
                .andExpect(jsonPath("$.data.umbrellaResponsePage[0].uuid").value(umbrellaModifyRequest.getUuid()))
                .andExpect(jsonPath("$.data.umbrellaResponsePage[0].rentable").value(umbrellaModifyRequest.isRentable()));
    }


    @DisplayName("관리자는 우산 정보를 삭제할 수 있다.")
    @Test
    void deleteUmbrellaTest() throws Exception {

        // given
        long id = umbrellaList.get(0).getId();

        // when & then
        mockMvc.perform(
                        delete("/admin/umbrellas/{umbrellaId}", id)
                ).andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/admin/umbrellas")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.umbrellaResponsePage").exists())
                .andExpect(jsonPath("$.data.umbrellaResponsePage.length()").value(umbrellaList.size() - 1));
    }


    @DisplayName("관리자는 전체 우산 통계를 조회할 수 있다.")
    @Test
    void showAllUmbrellasStatisticsTest() throws Exception {

        // when & then
        mockMvc.perform(
                        get("/admin/umbrellas/statistics")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.totalUmbrellaCount").value(6))
                .andExpect(jsonPath("$.data.rentableUmbrellaCount").value(3))
                .andExpect(jsonPath("$.data.rentedUmbrellaCount").value(2))
                .andExpect(jsonPath("$.data.missingUmbrellaCount").value(1))
                .andExpect(jsonPath("$.data.missingRate").value(16))
                .andExpect(jsonPath("$.data.totalRentCount").value(0));

    }


    @DisplayName("관리자는 지점 우산 통계를 조회할 수 있다.")
    @Test
    void success() throws Exception {

        // when & then
        mockMvc.perform(
                        get("/admin/umbrellas/statistics/{storeId}", storeMeta.getId())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.totalUmbrellaCount").value(6))
                .andExpect(jsonPath("$.data.rentableUmbrellaCount").value(3))
                .andExpect(jsonPath("$.data.rentedUmbrellaCount").value(2))
                .andExpect(jsonPath("$.data.missingUmbrellaCount").value(1))
                .andExpect(jsonPath("$.data.missingRate").value(16))
                .andExpect(jsonPath("$.data.totalRentCount").value(0));
    }

}
