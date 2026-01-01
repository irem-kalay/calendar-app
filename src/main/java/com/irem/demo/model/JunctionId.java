package com.irem.demo.model;

import java.io.Serializable;
import java.util.Objects;

public class JunctionId implements Serializable {
    private Long holidayId;
    private Long regionId;
    private Long personTypeId;

    public JunctionId() {}

    public JunctionId(Long holidayId, Long regionId, Long personTypeId) {
        this.holidayId = holidayId;
        this.regionId = regionId;
        this.personTypeId = personTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JunctionId)) return false;
        JunctionId that = (JunctionId) o;
        return Objects.equals(holidayId, that.holidayId) &&
               Objects.equals(regionId, that.regionId) &&
               Objects.equals(personTypeId, that.personTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(holidayId, regionId, personTypeId);
    }
}
