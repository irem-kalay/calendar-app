package com.irem.demo.service;

import com.irem.demo.dto.PersonTypeResponse;
import com.irem.demo.mapper.PersonTypeMapper;
import com.irem.demo.model.PersonType; // Model sınıfını import ediyoruz
import com.irem.demo.repository.PersonTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PersonTypeServiceTest {

    @Mock
    private PersonTypeRepository personTypeRepository;

    @Mock
    private PersonTypeMapper personTypeMapper;

    @InjectMocks
    private PersonTypeService personTypeService;

    @Test
    void testGetAllPersonTypes() {
        // Given
        PersonType entity1 = new PersonType();
        entity1.setId(1L);
        entity1.setName("Student");

        PersonType entity2 = new PersonType();
        entity2.setId(2L);
        entity2.setName("Employee");

        PersonType entity3 = new PersonType();
        entity3.setId(2L);
        entity3.setName("deneme");

        List<PersonType> mockEntityList = Arrays.asList(entity1, entity2, entity3);

        when(personTypeRepository.findAll()).thenReturn(mockEntityList);

        List<PersonTypeResponse> mockResponseList = Arrays.asList(
                new PersonTypeResponse(1L, "Student"),
                new PersonTypeResponse(2L, "Employee"),
                new PersonTypeResponse(3L, "deneme")
        );

        when(personTypeMapper.toDtoList(mockEntityList)).thenReturn(mockResponseList);

        // When
        List<PersonTypeResponse> result = personTypeService.getAllPersonTypes();

        // Then i m cehecking if it is realy working true.
        assertEquals(3, result.size());
        assertEquals("Student", result.get(0).getName());
        assertEquals("Employee", result.get(1).getName());
        assertEquals("deneme", result.get(2).getName()); // will the deneme name come witj index 2
        verify(personTypeRepository, times(1)).findAll();
        verify(personTypeMapper, times(1)).toDtoList(mockEntityList);
    }


    @Test
    void testGetAllPersonTypes_empty(){ //edge case null değerleri dönüyor mu

        PersonType person1 = new PersonType(); //mock object
        person1.setId(null);
        person1.setName(null);
        List<PersonType> mockEntityList = Arrays.asList(person1);

        when(personTypeRepository.findAll()).thenReturn(mockEntityList);

    // Mapper null değerler dönücek mi
        List<PersonTypeResponse> mockResponseList = Arrays.asList(
            new PersonTypeResponse(null, null)
        );

        when(personTypeMapper.toDtoList(mockEntityList)).thenReturn(mockResponseList);

    // result a getAllPersonTypes methodunu çağırıyorum
        List<PersonTypeResponse> result= personTypeService.getAllPersonTypes();

    // Assert (sonuçları kontrol et)
        assertEquals(1, result.size());
        assertNull(result.get(0).getId()); //verilen değerin Null olup olmadığına bakıyor
        assertEquals(null, result.get(0).getId()); //assertEquals la aynı şeyi yaptım
        assertNull(result.get(0).getName());

    // Mockların çağrıldığını da doğrula
        verify(personTypeRepository, times(1)).findAll();
        verify(personTypeMapper, times(1)).toDtoList(mockEntityList);
    }
}
