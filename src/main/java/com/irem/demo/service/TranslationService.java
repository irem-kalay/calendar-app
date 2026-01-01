package com.irem.demo.service;

import com.irem.demo.dto.TranslatedDTO;
import com.irem.demo.mapper.TranslationMapper;
import com.irem.demo.model.Translation;
import com.irem.demo.repository.TranslationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TranslationService {

    private final TranslationRepository translationRepository;

    public TranslationService(TranslationRepository translationRepository) {
        this.translationRepository = translationRepository;
    }

    public Map<Long, String> getTranslationsAsMap(String tableName, String langCode) {
        List<Translation> translations = translationRepository.findByTableNameAndLangCode(tableName, langCode);

        List<TranslatedDTO> dtos = translations.stream()
                .map(TranslationMapper::toDto)
                .toList();

        return dtos.stream()
                .collect(Collectors.toMap(TranslatedDTO::getId, TranslatedDTO::getName));
    }
}
