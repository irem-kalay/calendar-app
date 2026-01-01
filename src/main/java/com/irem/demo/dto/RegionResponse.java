package com.irem.demo.dto;

public class RegionResponse {
    private Long id;
    private String countryName;
    private String countryCode;

    public RegionResponse(Long id, String countryName, String countryCode) {
        this.id = id;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public Long getId() {
        return id;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
