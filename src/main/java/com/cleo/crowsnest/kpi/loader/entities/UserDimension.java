package com.cleo.crowsnest.kpi.loader.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table( name = "DimUser" )
@Getter
@Setter
public class UserDimension {
    @Id
    @Column(length = 40)
    private String id;
    @Column(length = 25, nullable = false)
    private String lastName;
    @Column(length = 25, nullable = false)
    private String firstName;
    @Column(length = 60)
    private String email;
    @Column(length = 40, nullable = false)
    private String organizationId;

    @Override
    public String toString() {
        return new StringBuilder()
                .append(id)
                .append("|")
                .append(email)
                .append("|")
                .append(firstName)
                .append("|")
                .append(lastName)
                .append("|")
                .append(organizationId)
                .toString();
    }
}
