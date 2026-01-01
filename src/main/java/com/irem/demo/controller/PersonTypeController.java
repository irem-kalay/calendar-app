package com.irem.demo.controller;

import com.irem.demo.dto.PersonTypeResponse;
import com.irem.demo.service.PersonTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persontypes")
public class PersonTypeController {

    private final PersonTypeService personTypeService;

    public PersonTypeController(PersonTypeService personTypeService) {
        this.personTypeService = personTypeService;
    }

    @GetMapping
    public List<PersonTypeResponse> getAllPersonTypes() {
        return personTypeService.getAllPersonTypes();
    }
}
