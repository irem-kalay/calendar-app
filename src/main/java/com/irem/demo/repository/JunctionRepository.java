package com.irem.demo.repository;

import com.irem.demo.model.Junction;
import com.irem.demo.model.JunctionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Set;

public interface JunctionRepository extends JpaRepository<Junction, JunctionId> {

    @Query("""
        SELECT j.holidayId 
        FROM Junction j 
        WHERE j.regionId = :regionId AND j.personTypeId IN :personTypeIds
    """)
    Set<Long> findHolidayIdsByRegionAndPersonTypes(@Param("regionId") Long regionId,
                                                   @Param("personTypeIds") List<Long> personTypeIds);
}
