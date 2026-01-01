package com.irem.demo.service;

import com.irem.demo.dto.FixedHolidayResponse;
import com.irem.demo.dto.HolidaySummary;
import com.irem.demo.repository.HolidayRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Arrays;

@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;

    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public List<HolidaySummary> getHolidaySummary(Long regionId, Long personTypeId) {
    List<Long> personTypeIds;

    if (personTypeId == 1L) {
        // sadece 1 için sorgula
        personTypeIds = List.of(1L);
    } else {
        // diğerleri için hem 1 hem istenen id
        personTypeIds = Arrays.asList(1L, personTypeId);
    }
    return holidayRepository.getHolidaySummaryByRegionAndPersonTypeIds(regionId, personTypeIds);
}
//fixed holidays için ayrı endpoint yazdım mevcutta olan verilerle karışmasın diye
 @GetMapping("/fixed")
public List<FixedHolidayResponse> getFixedHolidays(
    @RequestParam Long regionId,
    @RequestParam List<Long> personTypeIds,
    @RequestParam(required = false) Integer year //yıl artık opsiyonel, veya hiç kullanılmayabilir
) {
    // Yıl verisi önemli değil artık, fixed tatiller her yıl için aynı
    return holidayRepository.getFixedHolidaysIgnoringYear(regionId, personTypeIds);
}


}
