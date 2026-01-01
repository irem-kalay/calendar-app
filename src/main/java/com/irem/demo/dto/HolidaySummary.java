package com.irem.demo.dto;
//implementasyon springboot tarafında çalışırken otomatik oluşuyo
public interface HolidaySummary {
    Long getDefinitionId();          // holiday_definitions.id
    String getHolidayName();         // holiday_definitions.name
    String getHolidayType();         // holiday_types.name
    Integer getDurationDays();       // tatilin toplam gün sayısı
    String getNotes();               // holidays.notes
    String getMonthDay();   // TO_CHAR(h.calendar_date, 'MM-DD')
    Long getHolidayTypeId();
    Long getHolidayId();  //translation için holidays tablosunun idsine ihtiyaç var
}
