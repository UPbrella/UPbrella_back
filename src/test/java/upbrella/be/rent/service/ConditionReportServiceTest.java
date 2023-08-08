package upbrella.be.rent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.rent.dto.request.HistoryFilterRequest;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.ConditionReportPageResponse;
import upbrella.be.rent.dto.response.ConditionReportResponse;
import upbrella.be.rent.entity.ConditionReport;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.repository.ConditionReportRepository;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ConditionReportServiceTest {

    @Mock
    private ConditionReportRepository conditionReportRepository;
    @InjectMocks
    private ConditionReportService conditionReportService;
    private StoreMeta foundStoreMeta;
    private Umbrella foundUmbrella;
    private User userToRent;
    private History history;
    private ConditionReport conditionReport;

    @BeforeEach
    void setUp() {

        foundStoreMeta = StoreMeta.builder()
                .id(25L)
                .name("motive study cafe")
                .deleted(false)
                .build();

        foundUmbrella = Umbrella.builder()
                .id(99L)
                .uuid(99L)
                .deleted(false)
                .storeMeta(foundStoreMeta)
                .rentable(true)
                .build();

        userToRent = User.builder()
                .id(11L)
                .name("테스터")
                .phoneNumber("010-1234-5678")
                .adminStatus(false)
                .build();

        history = History.builder()
                .id(33L)
                .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                .returnedAt(LocalDateTime.of(1000, 12, 3, 4, 25))
                .refundedAt(LocalDateTime.of(1000, 12, 3, 4, 26))
                .refundedBy(userToRent)
                .returnStoreMeta(foundStoreMeta)
                .umbrella(foundUmbrella)
                .user(userToRent)
                .rentStoreMeta(foundStoreMeta)
                .build();

        conditionReport = ConditionReport.builder()
                .id(1L)
                .content("content")
                .history(history)
                .etc("etc")
                .build();
    }

    @Nested
    @DisplayName("사용자(관리자)는 상태 신고 내역 조회를 할 수 있다.")
    class findConditionReportsTest {

        @Test
        @DisplayName("사용자(관리자)는 상태 신고 내역 조회를 할 수 있다.")
        void success() {

            // given
            ConditionReportPageResponse conditionReportsResponse = ConditionReportPageResponse.builder()
                    .conditionReports(
                            List.of(
                                    ConditionReportResponse.builder()
                                            .id(33L)
                                            .umbrellaUuid(99L)
                                            .content("content")
                                            .etc("etc")
                                            .build())
                    ).build();

            given(conditionReportRepository.findAll())
                    .willReturn(List.of(conditionReport));

            // when
            ConditionReportPageResponse allConditionReports = conditionReportService.findAll();

            // then
            assertAll(() -> assertThat(allConditionReports)
                            .usingRecursiveComparison()
                            .isEqualTo(conditionReportsResponse),
                    () -> assertThat(allConditionReports.getConditionReports().size())
                            .isEqualTo(1),
                    () -> then(conditionReportRepository).should(times(1))
                            .findAll()
            );
        }
    }

}