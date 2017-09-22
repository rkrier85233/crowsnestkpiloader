package com.cleo.crowsnest.kpi.loader.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table( name = "DimDataFlow" )
@Getter
@Setter
public class DataFlowDimension {
    @Id
    @Column(length = 40)
    private String id;
    @Column(length = 60, nullable = false)
    private String name;
    private boolean scheduled;
    private int interval;
    @Column(length = 15)
    private String timeUnit;
    @Column(length = 10, nullable = false)
    private String operation;
    @Column(length = 40, nullable = false)
    private String organizationId;
    @Column(length = 40, nullable = false)
    private String sourceDatastoreId;
    @Column(length = 40, nullable = false)
    private String destinationDatastoreId;

    @Override
    public String toString() {
        return new StringBuilder()
                .append(id)
                .append("|")
                .append(destinationDatastoreId)
                .append("|")
                .append(interval)
                .append("|")
                .append(name)
                .append("|")
                .append(operation)
                .append("|")
                .append(organizationId)
                .append("|")
                .append(scheduled)
                .append("|")
                .append(sourceDatastoreId)
                .append("|")
                .append(timeUnit)
                .toString();
    }
}
