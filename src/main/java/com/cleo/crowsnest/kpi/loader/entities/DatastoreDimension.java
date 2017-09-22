package com.cleo.crowsnest.kpi.loader.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table( name = "DimDatastore" )
@Getter
@Setter
public class DatastoreDimension {
    @Id
    @Column(length = 40)
    private String id;
    @Column(length = 60)
    private String name;
    @Column(length = 255)
    private String path;
    @Column(length = 40, nullable = false)
    private String organizationId;
    @Column(length = 40, nullable = false)
    private String accessPointId;

    @Override
    public String toString() {
        return new StringBuilder()
                .append(id)
                .append("|")
                .append(accessPointId)
                .append("|")
                .append(name)
                .append("|")
                .append(organizationId)
                .append("|")
                .append(path)
                .toString();
    }
}
