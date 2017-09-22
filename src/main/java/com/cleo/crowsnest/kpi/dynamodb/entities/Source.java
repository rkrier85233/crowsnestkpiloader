package com.cleo.crowsnest.kpi.dynamodb.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.cleo.crowsnest.kpi.loader.entities.DatastoreDimension;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class Source {
    private String datastoreId;
    private String datastoreName;
    private String datastoreOwnerId;
    private String accessPointId;
    private String itemMatch;

    // Public no arg constructor needed by persistence layer.
    public Source() {
    }

    @Builder
    private Source(DatastoreDimension datastore, String userId) {
        this.datastoreId = datastore.getId();
        this.datastoreName = datastore.getName();
        this.datastoreOwnerId = userId;
        this.accessPointId = datastore.getAccessPointId();
        this.itemMatch = "ALL";
    }
}
