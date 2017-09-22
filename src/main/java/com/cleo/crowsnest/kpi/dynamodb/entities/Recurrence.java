package com.cleo.crowsnest.kpi.dynamodb.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class Recurrence {
    private boolean enabled;
    private Integer interval;
    private String timeUnit;

    // Public no arg constructor needed by persistence layer.
    public Recurrence() {
    }

    @Builder
    private Recurrence(Integer interval, String timeUnit) {
        this.enabled = true;
        this.interval = interval;
        this.timeUnit = timeUnit;
    }
}
