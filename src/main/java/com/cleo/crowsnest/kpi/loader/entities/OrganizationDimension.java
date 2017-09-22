package com.cleo.crowsnest.kpi.loader.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table( name = "DimOrganization" )
@Getter
@Setter
public class OrganizationDimension {
    @Id
    @Column(length = 40)
    private String id;
    @Column(length = 60, nullable = false)
    private String name;
    @Column(length = 40, nullable = false)
    private String poolName;

    @Override
    public String toString() {
        return new StringBuilder()
                .append(id)
                .append("|")
                .append(name)
                .append("|")
                .append(poolName)
                .toString();
    }
}
