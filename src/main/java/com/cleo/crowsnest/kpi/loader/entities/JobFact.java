package com.cleo.crowsnest.kpi.loader.entities;

import org.apache.log4j.helpers.DateTimeDateFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fact_job")
@Getter
@Setter
public class JobFact {
    @Id
    @Column(length = 40)
    private String id;

    @Column(nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    private Date startDate;
    @Column(nullable = true)
    @Temporal(value = TemporalType.TIMESTAMP)
    @Setter(AccessLevel.NONE)
    private Date endDate;
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private Long startDateEpoch;
    @Column(nullable = true)
    @Setter(AccessLevel.NONE)
    private Long endDateEpoch;
    @Column(length = 40, nullable = false)
    private String sourceAccessPointId;
    @Column(length = 40, nullable = false)
    private String destAccessPointId;
    @Column(length = 40, nullable = false)
    private String sourceDatastoreId;
    @Column(length = 40, nullable = false)
    private String destDatastoreId;
    @Column(length = 40, nullable = false)
    private String userId;
    @Column(length = 40, nullable = false)
    private String organizationId;
    @Column(length = 40, nullable = false)
    private String dataflowId;
    @Column(nullable = true)
    private Long duration;
    @Column(nullable = true)
    private Integer numberItems;
    @Column(nullable = true)
    private Long bytesTransferred;
    @Column(nullable = true)
    private String status;
    @Column(nullable = true)
    private Double throughput;

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        startDateEpoch = startDate == null ? null : startDate.getTime();
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        endDateEpoch = endDate == null ? null : endDate.getTime();
    }

    @Override
    public String toString() {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss-07");
        return new StringBuilder()
                .append(id)
                .append("|")
                .append(bytesTransferred)
                .append("|")
                .append(dataflowId)
                .append("|")
                .append(destAccessPointId)
                .append("|")
                .append(destDatastoreId)
                .append("|")
                .append(duration)
                .append("|")
                .append(fmt.format(endDate))
                .append("|")
                .append(endDateEpoch)
                .append("|")
                .append(numberItems)
                .append("|")
                .append(organizationId)
                .append("|")
                .append(sourceAccessPointId)
                .append("|")
                .append(sourceDatastoreId)
                .append("|")
                .append(fmt.format(startDate))
                .append("|")
                .append(startDateEpoch)
                .append("|")
                .append(status)
                .append("|")
                .append(throughput)
                .append("|")
                .append(userId)
                .toString();
    }
}
