package upbrella.be.store.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.store.repository.StoreMetaRepository;

@Transactional
@SpringBootTest
class StoreMetaServiceTest {

    @Autowired
    private StoreMetaRepository storeMetaRepository;
    @Autowired
    private StoreMetaService storeMetaService;

    @Test
    @DisplayName("존재하는 협업지점을 조회시 정상 조회된다.")
    void findByIdTest() {
        // given
        StoreMeta storeMeta = StoreMeta.builder()
                .name("업브렐라")
                .category("카페")
                .classification("카페")
                .subClassification("커피전문점")
                .thumbnail("https://upbrella.co.kr/images/logo.png")
                .activated(true)
                .latitude(37.503716)
                .longitude(127.053718)
                .build();

        // when
        StoreMeta savedStoreMeta = storeMetaRepository.save(storeMeta);
        StoreMeta foundStoreMeta = storeMetaService.findById(savedStoreMeta.getId());

        // then
        Assertions.assertThat(foundStoreMeta.getId()).isEqualTo(savedStoreMeta.getId());
    }

    @Test
    @DisplayName("존재하지 않는 협업지점을 조회시 예외가 발생한다.")
    void test() {
        // given


        // when


        // then
        Assertions.assertThatThrownBy(() -> storeMetaService.findById(999999999L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
