package com.irem.demo.dto;
//class
public class PersonTypeResponse {
    private Long id;
    private String name;

    public PersonTypeResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
