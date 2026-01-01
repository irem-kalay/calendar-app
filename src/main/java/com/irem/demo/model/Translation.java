package com.irem.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "translations")
@IdClass(TranslationId.class)
public class Translation {

    @Id
    @Column(name = "table_name")
    private String tableName;

    @Id
    @Column(name = "record_id")
    private Long recordId;

    @Id
    @Column(name = "lang_code")
    private String langCode;

    @Column(name = "translation_text", nullable = false)
    private String translationText;

    // Getters and Setters
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }

    public String getLangCode() { return langCode; }
    public void setLangCode(String langCode) { this.langCode = langCode; }

    public String getTranslationText() { return translationText; }
    public void setTranslationText(String translationText) { this.translationText = translationText; }
}
