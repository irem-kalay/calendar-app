package com.irem.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "junction")
@IdClass(JunctionId.class)
public class Junction {

    @Id
    private Long holidayId;

    @Id
    private Long regionId;

    @Id
    private Long personTypeId;

    // Getters and Setters
    public Long getHolidayId() { return holidayId; }
    public void setHolidayId(Long holidayId) { this.holidayId = holidayId; }

    public Long getRegionId() { return regionId; }
    public void setRegionId(Long regionId) { this.regionId = regionId; }

    public Long getPersonTypeId() { return personTypeId; }
    public void setPersonTypeId(Long personTypeId) { this.personTypeId = personTypeId; }
}
