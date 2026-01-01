package com.irem.demo.dto;

public class WorkdayResponse {
    private double totalWorkdays;

    public WorkdayResponse(double totalWorkdays) {
        this.totalWorkdays = totalWorkdays;
    }

    public double getTotalWorkdays() {
        return totalWorkdays;
    }

    public void setTotalWorkdays(double totalWorkdays) {
        this.totalWorkdays = totalWorkdays;
    }
}

