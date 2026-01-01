package com.irem.demo.controller;

import com.irem.demo.dto.RegionResponse;
import com.irem.demo.service.RegionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public List<RegionResponse> getRegions() {
        return regionService.getAllRegions();
    }
}
