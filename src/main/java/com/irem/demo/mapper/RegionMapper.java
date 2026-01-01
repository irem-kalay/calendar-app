package com.irem.demo.mapper;

import com.irem.demo.dto.RegionResponse;
import com.irem.demo.model.Region;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RegionMapper {

    public RegionResponse toRegionResponse(Region region) {
        if (region == null) {
            return null;
        }
        return new RegionResponse(region.getId(), region.getCountryName(), region.getCountryCode());
    }

    public List<RegionResponse> toRegionResponseList(List<Region> regions) {
        if (regions == null) {
            return null;
        }
        return regions.stream()
                .map(this::toRegionResponse)
                .collect(Collectors.toList());
    }
}