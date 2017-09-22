package com.cleo.crowsnest.kpi.loader.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table( name = "DimAccessPoint" )
@Getter
@Setter
public class AccessPointDimension {
    @Id
    @Column(length = 40)
    private String id;
    @Column(length = 50, nullable = false)
    private String name;
    @Column(length = 15, nullable = false)
    private String platform;
    @Column(length = 10)
    private String platformVersion;
    @Column(length = 100, nullable = false)
    private String transportUri;
    @Column(length = 40, nullable = false)
    private String organizationId;
    @Column(length = 40, nullable = false)
    private String userId;

    @Override
    public String toString() {
        return new StringBuilder()
                .append(id)
                .append("|")
                .append(name)
                .append("|")
                .append(organizationId)
                .append("|")
                .append(platform)
                .append("|")
                .append(platformVersion)
                .append("|")
                .append(transportUri)
                .append("|")
                .append(userId)
                .toString();
    }
}
