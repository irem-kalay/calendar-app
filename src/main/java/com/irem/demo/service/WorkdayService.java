package com.irem.demo.service;

import com.irem.demo.dto.HolidayBlockResponse;
import com.irem.demo.model.Holiday;
import com.irem.demo.repository.HolidayRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;


@Service
public class WorkdayService {

    private final HolidayRepository holidayRepository;

    public WorkdayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }
//iş günü hesaplama için
    public double calculateWorkdays(LocalDate start, LocalDate end, Long regionId, List<Long> personTypeIds) {
        double workdays = 0.0;
        
        List<Holiday> holidays = holidayRepository.findFilteredHolidays(start, end, regionId, personTypeIds);

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) { //koşulu !date.isAfter yerine date.isBefore olarak değiştim
            final LocalDate currentDate = date;

            if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                continue;

            boolean isHoliday = false;
            double holidayFraction = 0.0;

            for (Holiday h : holidays) {
                if (h.getCalendarDate().equals(currentDate)) {
                    isHoliday = true;
                    holidayFraction = (h.getIsFullDay() == 2) ? 0.5 : 0.0;
                    break;
                }
            }

            if (!isHoliday) {
                workdays += 1.0;
            } else {
                workdays += holidayFraction;
            }
        }

        return workdays;
    }

public List<HolidayBlockResponse> findHolidayBlocks(LocalDate start, LocalDate end, Long regionId, List<Long> personTypeIds) {
    if (!personTypeIds.contains(1L)) {
        List<Long> newPersonTypeIds = new ArrayList<>(personTypeIds);
        newPersonTypeIds.add(1L);
        personTypeIds = newPersonTypeIds;
    }

    List<Holiday> holidays = holidayRepository.findFilteredHolidays(start, end, regionId, personTypeIds);

    Set<LocalDate> holidayDates = holidays.stream()
            .map(Holiday::getCalendarDate)
            .collect(Collectors.toSet());

    List<HolidayBlockResponse> blocks = new ArrayList<>();
    LocalDate current = start;

    while (!current.isAfter(end)) {
        if (holidayDates.contains(current)) {

            List<Holiday> blockHolidays = new ArrayList<>();
            LocalDate blockStart = current;
            LocalDate blockEnd = current;

            while (blockStart.minusDays(1).getDayOfWeek() == DayOfWeek.SATURDAY
                    || blockStart.minusDays(1).getDayOfWeek() == DayOfWeek.SUNDAY) {
                blockStart = blockStart.minusDays(1);
                if (blockStart.isBefore(start)) break;
            }

            boolean extended;
            do {
                extended = false;
                LocalDate nextDay = blockEnd.plusDays(1);

                while (!nextDay.isAfter(end)
                        && (holidayDates.contains(nextDay)
                        || nextDay.getDayOfWeek() == DayOfWeek.SATURDAY
                        || nextDay.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                    blockEnd = nextDay;
                    nextDay = blockEnd.plusDays(1);
                    extended = true;
                }

                if (!nextDay.isAfter(end) && holidayDates.contains(nextDay)) {
                    blockEnd = nextDay;
                    extended = true;
                }
            } while (extended);

            LocalDate bs = blockStart;
            LocalDate be = blockEnd;

            blockHolidays = holidays.stream()
                    .filter(h -> !h.getCalendarDate().isBefore(bs) && !h.getCalendarDate().isAfter(be))
                    .collect(Collectors.toList());

            String holidayNames = blockHolidays.stream()
                    .map(h -> h.getPeriod().getDefinition().getName())
                    .distinct()
                    .collect(Collectors.joining(", "));

            // Burada sadece ilk tatilin definitionId'sini alıyoruz
            Long definitionId = null;
            if (!blockHolidays.isEmpty()) {
                definitionId = blockHolidays.get(0).getPeriod().getDefinition().getId();
            }

            int totalDays = (int) (blockEnd.toEpochDay() - blockStart.toEpochDay() + 1);

            blocks.add(new HolidayBlockResponse(blockStart, blockEnd, totalDays, holidayNames, definitionId));

            current = blockEnd.plusDays(1);
        } else {
            current = current.plusDays(1);
        }
    }

    return blocks;
}





}

