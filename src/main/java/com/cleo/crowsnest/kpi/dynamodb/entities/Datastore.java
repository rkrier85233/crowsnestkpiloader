package com.cleo.crowsnest.kpi.dynamodb.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGenerateStrategy;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedTimestamp;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.cleo.crowsnest.kpi.loader.entities.AccessPointDimension;
import com.cleo.crowsnest.kpi.loader.entities.DatastoreDimension;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@DynamoDBTable(tableName = "Datastore")
public class Datastore {
    @DynamoDBHashKey
    private String organizationId;
    @DynamoDBRangeKey
    private String id;
    private String accessPointName;
    private String name;
    private String lowerCaseName;
    private String path;
    private Date created;
    private Map<String, String> ownerAttributes;
    @DynamoDBAutoGeneratedTimestamp(strategy = DynamoDBAutoGenerateStrategy.ALWAYS)
    private Date updated;
    private String ownerId;

    @DynamoDBIgnore
    private String accessPointId;

    // Public no arg constructor needed by persistence layer.
    public Datastore() {
    }

    @Builder
    private Datastore(@NonNull AccessPointDimension accessPointDimension, @NonNull DatastoreDimension datastoreDimension, @NonNull Map<String, String> userAttributes, @NonNull Instant created) {
        this.id = datastoreDimension.getAccessPointId().concat(".").concat(datastoreDimension.getId());
        this.accessPointName = accessPointDimension.getName();
        this.name = datastoreDimension.getName();
        this.lowerCaseName = name.toLowerCase();
        this.path = datastoreDimension.getPath();
        this.ownerId = userAttributes.get("sub");
        this.ownerAttributes = userAttributes;
        this.organizationId = accessPointDimension.getOrganizationId();
        this.accessPointId = datastoreDimension.getAccessPointId();
        this.created = Date.from(created);
    }
}
