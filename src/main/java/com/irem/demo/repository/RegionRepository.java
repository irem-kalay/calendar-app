package com.irem.demo.repository;

import com.irem.demo.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    @Query(value = """
    
        SELECT * FROM region
        WHERE countrycode = :countryCode

    """, nativeQuery = true)
    Optional<Region> findByCountryCode(@Param("countryCode") String countryCode);
}
