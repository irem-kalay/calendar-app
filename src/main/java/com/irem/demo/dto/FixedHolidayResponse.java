package com.irem.demo.dto;
//interface yazıyorum metodlarla veriyi alıyorum
public interface FixedHolidayResponse {
    String getHolidayName();
    String getHolidayType();
    Long getHolidayTypeId();      // EKLENDİ
    String getMonthDay();
    String getNotes();
    Long getDefinitionId();
}
