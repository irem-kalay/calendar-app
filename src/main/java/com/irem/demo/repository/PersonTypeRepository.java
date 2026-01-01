package com.irem.demo.repository;

import com.irem.demo.model.PersonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//name parametre
@Repository
public interface PersonTypeRepository extends JpaRepository<PersonType, Long> {
    @Query(value = 
        """
            SELECT * 
            FROM persontypes 
            WHERE name = :name 
            FETCH FIRST 1 ROWS ONLY
        """, nativeQuery = true)
    Optional<PersonType> findByName(@Param("name") String name);
}