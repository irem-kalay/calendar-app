package com.irem.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "holidays")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "calendar_date", nullable = false)
    private LocalDate calendarDate;

    @ManyToOne
    @JoinColumn(name = "period_id", nullable = false)
    private HolidayPeriod period;

    @Column(name = "is_full_day", nullable = false)
    private int isFullDay = 1;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getCalendarDate() { return calendarDate; }
    public void setCalendarDate(LocalDate calendarDate) { this.calendarDate = calendarDate; }

    public HolidayPeriod getPeriod() { return period; }
    public void setPeriod(HolidayPeriod period) { this.period = period; }

    public int getIsFullDay() { return isFullDay; }
    public void setIsFullDay(int isFullDay) { this.isFullDay = isFullDay; }
}
