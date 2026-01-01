package com.irem.demo.mapper;

import com.irem.demo.dto.HolidayBlockResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class HolidayMapper {

    public HolidayBlockResponse toHolidayBlockResponse(LocalDate startDate, LocalDate endDate, int totalDays, String holidayName, Long definitionId) {
        return new HolidayBlockResponse(startDate, endDate, totalDays, holidayName, definitionId);
    }
}
