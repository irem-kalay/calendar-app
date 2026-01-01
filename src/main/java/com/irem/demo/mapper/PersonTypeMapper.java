package com.irem.demo.mapper;

import com.irem.demo.dto.PersonTypeResponse;
import com.irem.demo.model.PersonType;
import org.springframework.stereotype.Component; // Veya @Mapper (MapStruct kullanılıyorsa)

import java.util.List;
import java.util.stream.Collectors;

@Component // Spring'in bu sınıfı bir bean olarak yönetmesini sağlar
public class PersonTypeMapper {

    // PersonType modelini PersonTypeResponse DTO'suna dönüştüren metot
    public PersonTypeResponse toDto(PersonType personType) {
        if (personType == null) {
            return null;
        }
        return new PersonTypeResponse(personType.getId(), personType.getName());
    }

    // PersonType modelleri listesini PersonTypeResponse DTO'ları listesine dönüştüren metot
    public List<PersonTypeResponse> toDtoList(List<PersonType> personTypes) {
        if (personTypes == null) {
            return null;
        }
        return personTypes.stream()
                .map(this::toDto) // Her bir PersonType'ı toDto metodu ile dönüştür
                .collect(Collectors.toList());
    }
}