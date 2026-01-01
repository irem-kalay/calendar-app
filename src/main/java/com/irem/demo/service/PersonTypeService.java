package com.irem.demo.service;

import com.irem.demo.dto.PersonTypeResponse;
import com.irem.demo.mapper.PersonTypeMapper;
//import com.irem.demo.model.PersonType;
import com.irem.demo.repository.PersonTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
//import java.util.stream.Collectors;

@Service
public class PersonTypeService {

    private final PersonTypeRepository personTypeRepository;
    private final PersonTypeMapper personTypeMapper; // Mapper'ı enjekte et

    // Constructor'a mapper'ı ekle
    public PersonTypeService(PersonTypeRepository personTypeRepository, PersonTypeMapper personTypeMapper) {
        this.personTypeRepository = personTypeRepository;
        this.personTypeMapper = personTypeMapper; // Mapper'ı ata
    }

    public List<PersonTypeResponse> getAllPersonTypes() {
        // Repository'den PersonType listesini al
        List<com.irem.demo.model.PersonType> personTypes = personTypeRepository.findAll();

        // Mapper kullanarak PersonType listesini PersonTypeResponse listesine dönüştür
        //artık service mapping yapmakla uğraşmıyo
        return personTypeMapper.toDtoList(personTypes);
    }
}
