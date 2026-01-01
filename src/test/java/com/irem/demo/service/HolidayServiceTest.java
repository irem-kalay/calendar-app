package com.irem.demo.service;

import com.irem.demo.dto.FixedHolidayResponse;
import com.irem.demo.dto.HolidaySummary;
import com.irem.demo.repository.HolidayRepository;
//import com.irem.demo.service.HolidayService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceTest {

    //repoyu mockluyorum
    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private HolidayService holidayService;

    @Test
    void testGetHolidaySummary_WithPersonTypeId1() {
        Long regionId = 1L;
        Long personTypeId = 1L;

//interface olduğu için dummy tekrar yazıyorum
        HolidaySummary mockSummary = new HolidaySummary() {
            public Long getDefinitionId() { return 10L; }
            public String getHolidayName() { return "Bayram"; }
            public String getHolidayType() { return "Resmi"; }
            public Integer getDurationDays() { return 1; }
            public String getNotes() { return "Resmi tatil"; }
            public String getMonthDay() { return "01-01"; }
            public Long getHolidayTypeId() { return 5L; }
            public Long getHolidayId() { return 100L; }
        };

        when(holidayRepository.getHolidaySummaryByRegionAndPersonTypeIds(eq(regionId), eq(List.of(1L))))
                .thenReturn(List.of(mockSummary));

        List<HolidaySummary> result = holidayService.getHolidaySummary(regionId, personTypeId);

        assertEquals(1, result.size());
        assertEquals("Bayram", result.get(0).getHolidayName());
        assertEquals("Resmi", result.get(0).getHolidayType());
    }

    @Test
    void testGetHolidaySummary_WithOtherPersonTypeId() {
        Long regionId = 2L;
        Long personTypeId = 3L;
        List<Long> expectedPersonTypeIds = List.of(1L, 3L);

//interface olduğu için dummy tekrar yazıyorum
        HolidaySummary mockSummary = new HolidaySummary() {
            public Long getDefinitionId() { return 11L; }
            public String getHolidayName() { return "23 Nisan"; }
            public String getHolidayType() { return "Ulusal"; }
            public Integer getDurationDays() { return 1; }
            public String getNotes() { return null; }
            public String getMonthDay() { return "04-23"; }
            public Long getHolidayTypeId() { return 6L; }
            public Long getHolidayId() { return 101L; }
        };

        when(holidayRepository.getHolidaySummaryByRegionAndPersonTypeIds(eq(regionId), eq(expectedPersonTypeIds)))
                .thenReturn(List.of(mockSummary));

        List<HolidaySummary> result = holidayService.getHolidaySummary(regionId, personTypeId);

        assertEquals(1, result.size());
        assertEquals("23 Nisan", result.get(0).getHolidayName());
        assertEquals("04-23", result.get(0).getMonthDay());
    }

    @Test
    void testGetFixedHolidays() {
        Long regionId = 1L;
        List<Long> personTypeIds = List.of(1L, 2L);
        Integer year = 2025;

//interface olduğu için dummy tekrar yazıyorum
        FixedHolidayResponse fixedHolidayResponse = new FixedHolidayResponse() {
            public String getHolidayName(){return "15 Temmuz";}
            public String getHolidayType(){return "Milli";}
            public Long getHolidayTypeId(){return 1L;}
            public String getMonthDay(){return "7";}
            public String getNotes(){return "14 temmuz";}
            public Long getDefinitionId(){return 510L;}
        };

    List<FixedHolidayResponse> fixedList = List.of(fixedHolidayResponse);

    when(holidayRepository.getFixedHolidaysIgnoringYear(eq(regionId), eq(personTypeIds)))
            .thenReturn(fixedList);

    List<FixedHolidayResponse> result = holidayService.getFixedHolidays(regionId, personTypeIds, year);

    assertEquals(1, result.size());
    assertEquals("15 Temmuz", result.get(0).getHolidayName());
    assertEquals("14 temmuz", result.get(0).getNotes());

    verify(holidayRepository, times(1))
            .getFixedHolidaysIgnoringYear(eq(regionId), eq(personTypeIds));
}
}
