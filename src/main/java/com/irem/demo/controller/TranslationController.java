package com.irem.demo.controller;

import com.irem.demo.service.TranslationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/translations")
public class TranslationController {

    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    /**
     * Örnek: /api/translations/table/persontypes?langCode=GB
     */
    @GetMapping("/table/{tableName}")
    public ResponseEntity<Map<Long, String>> getTableTranslations(
            @PathVariable String tableName,
            @RequestParam String langCode) {

        Map<Long, String> translations = translationService.getTranslationsAsMap(tableName, langCode);
        return ResponseEntity.ok(translations);
    }
}
