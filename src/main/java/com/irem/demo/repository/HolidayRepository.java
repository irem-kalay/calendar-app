package com.irem.demo.repository;
//eski halinde silme burayı

import com.irem.demo.dto.FixedHolidayResponse;
import com.irem.demo.dto.HolidaySummary;
import com.irem.demo.model.Holiday;
//import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
//total wordays bulmak için
    @Query("""
        SELECT h FROM Holiday h
        JOIN Junction j ON h.id = j.holidayId
        WHERE h.calendarDate BETWEEN :startDate AND :endDate
        AND j.regionId = :regionId
        AND j.personTypeId IN :personTypeIds
    """)
    List<Holiday> findFilteredHolidays(LocalDate startDate, LocalDate endDate, Long regionId, List<Long> personTypeIds);


    //tatil özeti kısmı
    @Query(value = """
    SELECT
    hd.id AS definitionId,
    hd.name AS holidayName,
    ht.name AS holidayType,
    ht.id AS holidayTypeId,
    h.id AS holidayId,
    COUNT(DISTINCT h.calendar_date) AS durationDays,
    h.notes AS notes,
    TO_CHAR(h.calendar_date, 'MM-DD') AS monthDay
FROM holidays h,
     holiday_periods hp,
     holiday_definitions hd,
     holiday_types ht,
     junction j
WHERE h.period_id = hp.id
  AND hp.definition_id = hd.id
  AND hd.type_id = ht.id
  AND h.id = j.holiday_id
  AND j.region_id = :regionId
  AND j.person_type_id IN (:personTypeIds)
GROUP BY hd.id, hd.name, ht.id, ht.name, h.id, h.notes, TO_CHAR(h.calendar_date, 'MM-DD')
ORDER BY MIN(h.calendar_date)

    """, nativeQuery = true)
List<HolidaySummary> getHolidaySummaryByRegionAndPersonTypeIds(
    @Param("regionId") Long regionId,
    @Param("personTypeIds") List<Long> personTypeIds);


    //fixed holidays için
@Query(value = """
        SELECT 
    hd.id AS definition_id,
    hd.name AS holiday_name,
    ht.name AS holiday_type,
    ht.id AS holiday_type_id,
    TO_CHAR(h.calendar_date, 'MM-DD') AS month_day,
    h.notes AS notes
FROM 
    holidays h,
    holiday_periods hp,
    holiday_definitions hd,
    holiday_types ht,
    junction j
WHERE 
    h.period_id = hp.id
    AND hp.definition_id = hd.id
    AND hd.type_id = ht.id
    AND h.id = j.holiday_id
    AND hd.is_fixed = 1 
    AND j.region_id = :regionId
    AND j.person_type_id IN (:personTypeIds)
GROUP BY 
    hd.id, hd.name, ht.name, ht.id, h.calendar_date, h.notes
ORDER BY 
    month_day

        """,nativeQuery = true)
List<FixedHolidayResponse> getFixedHolidaysIgnoringYear(
    @Param("regionId") Long regionId,
    @Param("personTypeIds") List<Long> personTypeIds
);



}

