package com.cleo.crowsnest.kpi.dynamodb.builder;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.cleo.crowsnest.kpi.dynamodb.entities.AccessPoint;
import com.cleo.crowsnest.kpi.dynamodb.entities.DataFlow;
import com.cleo.crowsnest.kpi.dynamodb.entities.Datastore;
import com.cleo.crowsnest.kpi.loader.entities.AccessPointDimension;
import com.cleo.crowsnest.kpi.loader.entities.DataFlowDimension;
import com.cleo.crowsnest.kpi.loader.entities.DatastoreDimension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.extern.slf4j.Slf4j;

import static java.time.temporal.ChronoUnit.MINUTES;

@Slf4j
public class DynamoDbSampleBuilder {

    public void buildAccessPoints(EntityManager em, DynamoDBMapper mapper, Instant startDate, Instant endDate, Map<String, String> userAttributes) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long minutesBetween = MINUTES.between(startDate, endDate);
        TypedQuery<AccessPointDimension> query = em.createQuery("SELECT x FROM AccessPointDimension x WHERE x.organizationId = :organizationId", AccessPointDimension.class);
        query.setParameter("organizationId", "us-west-2_xvKNTz8xN");
        List<AccessPointDimension> results = query.getResultList();
        results.forEach(ap -> {
            mapper.save(AccessPoint.builder()
                    .accessPointDimension(ap)
                    .created(startDate.plus(random.nextLong(minutesBetween), ChronoUnit.MINUTES))
                    .userAttributes(userAttributes)
                    .build());
            log.info("Created access point: {}", ap.getName());
            buildDatastores(em, mapper, ap, startDate, endDate, userAttributes);
        });
    }

    private void buildDatastores(EntityManager em, DynamoDBMapper mapper, AccessPointDimension accessPointDimension, Instant startDate, Instant endDate, Map<String, String> userAttributes) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long minutesBetween = MINUTES.between(startDate, endDate);
        TypedQuery<DatastoreDimension> query = em.createQuery("SELECT x FROM DatastoreDimension x WHERE x.accessPointId = :accessPointId", DatastoreDimension.class);
        query.setParameter("accessPointId", accessPointDimension.getId());
        List<DatastoreDimension> results = query.getResultList();
        results.forEach(ds -> {
            mapper.save(Datastore.builder()
                    .accessPointDimension(accessPointDimension)
                    .datastoreDimension(ds)
                    .created(startDate.plus(random.nextLong(minutesBetween), ChronoUnit.MINUTES))
                    .userAttributes(userAttributes)
                    .build());
            log.info("Created data store: {}", ds.getName());
        });
    }

    public void buildDataFlows(EntityManager em, DynamoDBMapper mapper, Instant startDate, Instant endDate, Map<String, String> userAttributes) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long minutesBetween = MINUTES.between(startDate, endDate);
        TypedQuery<DataFlowDimension> query = em.createQuery("SELECT x FROM DataFlowDimension x WHERE x.organizationId = :organizationId", DataFlowDimension.class);
        query.setParameter("organizationId", "us-west-2_xvKNTz8xN");
        List<DataFlowDimension> results = query.getResultList();
        results.forEach(df -> {
            DatastoreDimension srcDs = em.find(DatastoreDimension.class, df.getSourceDatastoreId());
            DatastoreDimension destDs = em.find(DatastoreDimension.class, df.getDestinationDatastoreId());
            mapper.save(DataFlow.builder()
                    .dataFlowDimension(df)
                    .srcDs(srcDs)
                    .destDs(destDs)
                    .userAttributes(userAttributes)
                    .created(startDate.plus(random.nextLong(minutesBetween), ChronoUnit.MINUTES))
                    .build());

            log.info("Created data flow: {}", df.getName());
        });
    }
}
