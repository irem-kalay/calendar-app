package com.irem.demo.service;

import com.irem.demo.dto.RegionResponse;
import com.irem.demo.repository.RegionRepository;
import com.irem.demo.mapper.RegionMapper; // Mapper'ı import et
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionService {

    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper; // Mapper ekledim

    public RegionService(RegionRepository regionRepository, RegionMapper regionMapper) {
        this.regionRepository = regionRepository;
        this.regionMapper = regionMapper; //constructorda mapper ekledim
    }

    public List<RegionResponse> getAllRegions() {
        return regionMapper.toRegionResponseList(regionRepository.findAll());
    }
}