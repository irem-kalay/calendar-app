package com.irem.demo.controller;

import com.irem.demo.dto.FixedHolidayResponse;
import com.irem.demo.dto.HolidaySummary;
import com.irem.demo.service.HolidayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping("/summary")
    public ResponseEntity<List<HolidaySummary>> getHolidaySummary(
            @RequestParam Long regionId,
            @RequestParam Long personTypeId) {
        List<HolidaySummary> summary = holidayService.getHolidaySummary(regionId, personTypeId);
        return ResponseEntity.ok(summary);
    }
//ayrı endpoint yazdım fixed holidays mevcuttaik verilerle karışmaması için
  @GetMapping("/fixed")
    public List<FixedHolidayResponse> getFixedHolidays(
            @RequestParam Long regionId,
            @RequestParam List<Long> personTypeIds,
            @RequestParam int year) {
        return holidayService.getFixedHolidays(regionId, personTypeIds, year);
    }


}

