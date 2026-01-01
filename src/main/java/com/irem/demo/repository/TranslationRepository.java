package com.irem.demo.repository;

import com.irem.demo.model.Translation;
import com.irem.demo.model.TranslationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, TranslationId> {

    @Query("""
        SELECT t 
        FROM Translation t 
        WHERE t.tableName = :tableName 
          AND t.langCode = :langCode
        """)
    List<Translation> findByTableNameAndLangCode(
        @Param("tableName") String tableName,
        @Param("langCode") String langCode
    );
}
