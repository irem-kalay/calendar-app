package com.irem.demo.service;

import com.irem.demo.model.Holiday;
//import com.irem.demo.model.HolidayPeriod;
//import com.irem.demo.model.HolidayDefinition;
import com.irem.demo.repository.HolidayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

//import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkdayServiceTest {

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private WorkdayService workdayService;

    @Test
    void testCalculateWorkdays_noHolidays() {
        LocalDate start = LocalDate.of(2024, 7, 1); // Pazartesi
        LocalDate end = LocalDate.of(2024, 7, 5);   // Cuma
        Long regionId = 1L;
        List<Long> personTypeIds = List.of(1L);

        when(holidayRepository.findFilteredHolidays(start, end, regionId, personTypeIds))
                .thenReturn(Collections.emptyList());

        double result = workdayService.calculateWorkdays(start, end, regionId, personTypeIds);

        // Pazartesi-Cuma arası 5 iş günü
        assertEquals(5.0, result);
    }

    @Test
    void testCalculateWorkdays_withFullHoliday() {
        LocalDate start = LocalDate.of(2024, 7, 1); // Pazartesi
        LocalDate end = LocalDate.of(2024, 7, 5);   // Cuma
        Long regionId = 1L;
        List<Long> personTypeIds = List.of(1L);

        Holiday holiday = new Holiday();
        holiday.setCalendarDate(LocalDate.of(2024, 7, 3)); // Çarşamba
        holiday.setIsFullDay(1); // Tam gün

        when(holidayRepository.findFilteredHolidays(start, end, regionId, personTypeIds))
                .thenReturn(List.of(holiday));

        double result = workdayService.calculateWorkdays(start, end, regionId, personTypeIds);

        // 5 iş günü - 1 tam gün tatil = 4.0
        assertEquals(4.0, result);
    }

    @Test
    void testCalculateWorkdays_withHalfHoliday() {
        LocalDate start = LocalDate.of(2024, 7, 1); // Pazartesi
        LocalDate end = LocalDate.of(2024, 7, 5);   // Cuma
        Long regionId = 1L;
        List<Long> personTypeIds = List.of(1L);

        Holiday holiday = new Holiday();
        holiday.setCalendarDate(LocalDate.of(2024, 7, 3)); // Çarşamba
        holiday.setIsFullDay(0); // Yarım gün

        when(holidayRepository.findFilteredHolidays(start, end, regionId, personTypeIds))
                .thenReturn(List.of(holiday));

        double result = workdayService.calculateWorkdays(start, end, regionId, personTypeIds);

        // 5 iş günü - 0.5 (yarım gün) = 4.5
        assertEquals(4.5, result);
    }

    @Test
    void testCalculateWorkdays_withWeekendAndHoliday() {
        LocalDate start = LocalDate.of(2024, 7, 5); // Cuma
        LocalDate end = LocalDate.of(2024, 7, 9);   // Salı
        Long regionId = 1L;
        List<Long> personTypeIds = List.of(1L);

        // Tatil Pazartesi (8 Temmuz)
        Holiday holiday = new Holiday();
        holiday.setCalendarDate(LocalDate.of(2024, 7, 8));
        holiday.setIsFullDay(1); // Tam gün

        when(holidayRepository.findFilteredHolidays(start, end, regionId, personTypeIds))
                .thenReturn(List.of(holiday));

        // İş günleri: 5 Tem (Cuma), 8 Tem (Pazartesi) [tatil], 9 Tem (Salı)
        // Toplam iş günü = 3 - 1 (tatil) = 2
        double result = workdayService.calculateWorkdays(start, end, regionId, personTypeIds);

        assertEquals(2.0, result);
    }
}

