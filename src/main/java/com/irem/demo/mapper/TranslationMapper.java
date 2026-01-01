package com.irem.demo.mapper;

import com.irem.demo.dto.TranslatedDTO;
import com.irem.demo.model.Translation;

public class TranslationMapper {

    public static TranslatedDTO toDto(Translation translation) {
        if (translation == null) return null;
        return new TranslatedDTO(
                translation.getRecordId(),
                translation.getTranslationText()
        );
    }
}
