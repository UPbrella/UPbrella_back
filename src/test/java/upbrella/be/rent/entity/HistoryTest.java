package upbrella.be.rent.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.store.entity.StoreMeta;
import upbrella.be.umbrella.entity.Umbrella;
import upbrella.be.user.dto.response.SingleHistoryResponse;
import upbrella.be.user.entity.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HistoryTest {

    private RentUmbrellaByUserRequest rentUmbrellaByUserRequest;
    private StoreMeta foundStoreMeta;
    private Umbrella foundUmbrella;
    private User userToRent;
    private History history;

    @BeforeEach
    void setUp() {
        rentUmbrellaByUserRequest = RentUmbrellaByUserRequest.builder()
                .region("신촌")
                .storeId(25L)
                .umbrellaId(99L)
                .conditionReport("상태 양호")
                .build();

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


    }

    @Test
    @DisplayName("반납 날짜 정보가 존재하지 않으면 미반납으로 처리하고, 반납 날짜를 대여날짜 +7일로 설정한다.")
    void notReturnedTest() {

        //given
        history = History.builder()
                .id(33L)
                .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                .returnedAt(null)
                .refundedAt(null)
                .refundedBy(userToRent)
                .returnStoreMeta(foundStoreMeta)
                .umbrella(foundUmbrella)
                .user(userToRent)
                .rentStoreMeta(foundStoreMeta)
                .build();

        SingleHistoryResponse expectedResponse = SingleHistoryResponse.builder()
                .umbrellaUuid(99L)
                .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                .returnAt(LocalDateTime.of(1000, 12, 3, 4, 24).plusDays(7))
                .rentedStore("motive study cafe")
                .isRefunded(false)
                .isReturned(false)
                .build();

        // when
        SingleHistoryResponse singleHistoryResponse = History.ofUserHistory(history);

        // then
        assertThat(singleHistoryResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("반납 날짜 정보가 존재하면 반납으로 처리하고, 반납 날짜를 그대로 표시한다.")
    void returnedTest() {

        //given
        history = History.builder()
                .id(33L)
                .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                .returnedAt(LocalDateTime.of(1000, 12, 3, 4, 25))
                .refundedAt(null)
                .refundedBy(userToRent)
                .returnStoreMeta(foundStoreMeta)
                .umbrella(foundUmbrella)
                .user(userToRent)
                .rentStoreMeta(foundStoreMeta)
                .build();

        SingleHistoryResponse expectedResponse = SingleHistoryResponse.builder()
                .umbrellaUuid(99L)
                .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                .returnAt(LocalDateTime.of(1000, 12, 3, 4, 25))
                .rentedStore("motive study cafe")
                .isRefunded(false)
                .isReturned(true)
                .build();

        // when
        SingleHistoryResponse singleHistoryResponse = History.ofUserHistory(history);

        // then
        assertThat(singleHistoryResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("환급 날짜 정보가 존재하지 않으면 미환급으로 처리한다.")
    void notRefundedTest() {

        //given
        history = History.builder()
                .id(33L)
                .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                .returnedAt(LocalDateTime.of(1000, 12, 3, 4, 25))
                .refundedAt(null)
                .refundedBy(userToRent)
                .returnStoreMeta(foundStoreMeta)
                .umbrella(foundUmbrella)
                .user(userToRent)
                .rentStoreMeta(foundStoreMeta)
                .build();

        SingleHistoryResponse expectedResponse = SingleHistoryResponse.builder()
                .umbrellaUuid(99L)
                .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                .returnAt(LocalDateTime.of(1000, 12, 3, 4, 25))
                .rentedStore("motive study cafe")
                .isRefunded(false)
                .isReturned(true)
                .build();

        // when
        SingleHistoryResponse singleHistoryResponse = History.ofUserHistory(history);

        // then
        assertThat(singleHistoryResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("환급 날짜 정보가 존재하면 환급으로 처리한다.")
    void refundedTest() {

        //given
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

        SingleHistoryResponse expectedResponse = SingleHistoryResponse.builder()
                .umbrellaUuid(99L)
                .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                .returnAt(LocalDateTime.of(1000, 12, 3, 4, 25))
                .rentedStore("motive study cafe")
                .isRefunded(true)
                .isReturned(true)
                .build();

        // when
        SingleHistoryResponse singleHistoryResponse = History.ofUserHistory(history);

        // then
        assertThat(singleHistoryResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("환급 처리할 유저와 환급 처리 시각을 입력받아 해당 대여 내역을 환급 확인한다.")
    void refundTest() {

        //given
        history = History.builder()
                .id(33L)
                .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                .returnedAt(LocalDateTime.of(1000, 12, 3, 4, 25))
                .returnStoreMeta(foundStoreMeta)
                .umbrella(foundUmbrella)
                .user(userToRent)
                .rentStoreMeta(foundStoreMeta)
                .build();

        // when
        history.refund(userToRent, LocalDateTime.of(1000, 1, 2, 3, 4, 5));

        // then
        assertAll(() -> history.getRefundedBy().equals(userToRent),
                  () -> history.getRefundedAt().equals(LocalDateTime.of(1000, 1, 2, 3, 4, 5)));
    }

    @Test
    @DisplayName("지불 처리할 유저와 처리 시각을 입력받아 해당 대여 내역을 지불 확인 처리한다.")
    void paidTest() {

        //given
        history = History.builder()
                .id(33L)
                .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
                .returnedAt(LocalDateTime.of(1000, 12, 3, 4, 25))
                .returnStoreMeta(foundStoreMeta)
                .umbrella(foundUmbrella)
                .user(userToRent)
                .rentStoreMeta(foundStoreMeta)
                .build();

        // when
        history.paid(userToRent, LocalDateTime.of(1000, 1, 2, 3, 4, 5));

        // then
        assertAll(() -> history.getPaidBy().equals(userToRent),
                () -> history.getPaidAt().equals(LocalDateTime.of(1000, 1, 2, 3, 4, 5)));
    }
}
