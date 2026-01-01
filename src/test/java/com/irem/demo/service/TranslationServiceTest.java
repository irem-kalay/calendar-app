package com.irem.demo.service;

//import com.irem.demo.dto.TranslatedDTO;
//import com.irem.demo.mapper.TranslationMapper;
import com.irem.demo.model.Translation;
import com.irem.demo.repository.TranslationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TranslationServiceTest {

    @Mock
    private TranslationRepository translationRepository;

    @InjectMocks
    private TranslationService translationService;

    @Test
    void testGetTranslationsAsMap() {
        // Arrange
        Translation translation1 = new Translation();
        translation1.setLangCode("TR");
        translation1.setRecordId(1L);
        translation1.setTableName("deneme");
        translation1.setTranslationText("denemetext");

        List<Translation> mockTranslationList = List.of(translation1);

        when(translationRepository.findByTableNameAndLangCode("deneme", "TR"))
                .thenReturn(mockTranslationList);

        // Act
        Map<Long, String> result = translationService.getTranslationsAsMap("deneme", "TR");

        // Assert
        assertEquals(1, result.size());
        assertEquals("denemetext", result.get(1L)); //denemetext in record id si 1 mi 

        verify(translationRepository, times(1))
                .findByTableNameAndLangCode("deneme", "TR");
    }
}
