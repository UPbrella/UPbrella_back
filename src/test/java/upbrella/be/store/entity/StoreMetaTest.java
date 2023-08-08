package upbrella.be.store.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StoreMetaTest {

    @Test
    @DisplayName("StoreMeta 삭제 테스트")
    void test() {

        // given
        Classification classification = Classification.builder()
                .id(1L)
                .type(ClassificationType.CLASSIFICATION)
                .name("카테고리")
                .latitude(33.33)
                .longitude(33.33)
                .build();

        Classification subClassification = Classification.builder()
                .id(2L)
                .type(ClassificationType.SUB_CLASSIFICATION)
                .name("카테고리")
                .build();

        BusinessHour businessHour = BusinessHour.builder()
                .id(1L)
                .date(DayOfWeek.MONDAY)
                .openAt(LocalTime.of(10, 0))
                .closeAt(LocalTime.of(20, 0))
                .build();

        StoreMeta storeMeta = StoreMeta.builder()
                .id(1L)
                .name("협업 지점명")
                .activated(true)
                .deleted(false)
                .classification(classification)
                .subClassification(subClassification)
                .category("카테고리")
                .latitude(33.33)
                .longitude(33.33)
                .password("비밀번호")
                .businessHours(Set.of(businessHour))
                .build();

        // when
        storeMeta.delete();

        // then
        assertThat(storeMeta.isDeleted()).isTrue();
    }
}
