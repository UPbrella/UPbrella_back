package upbrella.be.store.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.store.entity.BusinessHour;
import upbrella.be.store.entity.DayOfWeek;
import upbrella.be.store.repository.BusinessHourRepository;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
class BusinessHourServiceTest {

    @Mock
    private BusinessHourRepository businessHourRepository;
    @InjectMocks
    private BusinessHourService businessHourService;

    @Test
    @DisplayName("saveAllBusinessHour() 호출하면, 모든 요일이 저장된다.")
    void saveAllBusinessHour() {
        // given
        BusinessHour monday = BusinessHour.builder()
                .date(DayOfWeek.MONDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        BusinessHour tuesday = BusinessHour.builder()
                .date(DayOfWeek.TUESDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        BusinessHour wednesday = BusinessHour.builder()
                .date(DayOfWeek.WEDNESDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        BusinessHour thursday = BusinessHour.builder()
                .date(DayOfWeek.THURSDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        BusinessHour friday = BusinessHour.builder()
                .date(DayOfWeek.FRIDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        BusinessHour saturday = BusinessHour.builder()
                .date(DayOfWeek.SATURDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        BusinessHour sunday = BusinessHour.builder()
                .date(DayOfWeek.SUNDAY)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(18, 0))
                .build();
        List<BusinessHour> businessHours = List.of(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
        // when
        businessHourService.saveAllBusinessHour(businessHours);

        // then
        then(businessHourRepository).should().saveAll(businessHours);
    }
}
