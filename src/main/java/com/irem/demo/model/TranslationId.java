package com.irem.demo.model;

import java.io.Serializable;
import java.util.Objects;

public class TranslationId implements Serializable {
    private String tableName;
    private Long recordId;
    private String langCode;

    public TranslationId() {}

    public TranslationId(String tableName, Long recordId, String langCode) {
        this.tableName = tableName;
        this.recordId = recordId;
        this.langCode = langCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TranslationId)) return false;
        TranslationId that = (TranslationId) o;
        return Objects.equals(tableName, that.tableName) &&
               Objects.equals(recordId, that.recordId) &&
               Objects.equals(langCode, that.langCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, recordId, langCode);
    }
}
