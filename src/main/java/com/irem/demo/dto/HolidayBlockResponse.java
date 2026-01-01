package com.irem.demo.dto;

import java.time.LocalDate;

public class HolidayBlockResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalDays;
    private String holidayName;  // Mevcut
    private Long definitionId;   // Yeni eklenen alan

    public HolidayBlockResponse(LocalDate startDate, LocalDate endDate, int totalDays, String holidayName, Long definitionId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.holidayName = holidayName;
        this.definitionId = definitionId;
    }

    // getter/setterlar
    public String getHolidayName() { return holidayName; }
    public void setHolidayName(String holidayName) { this.holidayName = holidayName; }

    public Long getDefinitionId() { return definitionId; }
    public void setDefinitionId(Long definitionId) { this.definitionId = definitionId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getTotalDays() { return totalDays; }
    public void setTotalDays(int totalDays) { this.totalDays = totalDays; }
}
