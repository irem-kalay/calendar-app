package com.irem.demo.controller;

import com.irem.demo.dto.HolidayBlockResponse;
import com.irem.demo.dto.WorkdayResponse;
import com.irem.demo.service.WorkdayService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class WorkdayController {

    private final WorkdayService workdayService;

    public WorkdayController(WorkdayService workdayService) {
        this.workdayService = workdayService;
    }

    @GetMapping("/workdays")
    public WorkdayResponse calculateWorkdays(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            @RequestParam("regionId") Long regionId,
            @RequestParam("personTypeIds") List<Long> personTypeIds
    ) {
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        double total = workdayService.calculateWorkdays(startDate, endDate, regionId, personTypeIds);
        return new WorkdayResponse(total);
    }

    @GetMapping("/holiday-blocks")
public List<HolidayBlockResponse> getHolidayBlocks(
        @RequestParam("startDate") String startDateStr,
        @RequestParam("endDate") String endDateStr,
        @RequestParam("regionId") Long regionId,
        @RequestParam("personTypeIds") List<Long> personTypeIds
) {
    LocalDate startDate = LocalDate.parse(startDateStr);
    LocalDate endDate = LocalDate.parse(endDateStr);

    return workdayService.findHolidayBlocks(startDate, endDate, regionId, personTypeIds);
}

}
