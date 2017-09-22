package com.cleo.crowsnest.kpi.loader.builder;

import java.io.IOException;
import java.time.Instant;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("redshift-kpi");
        EntityManager em = emf.createEntityManager();
//        new OrganizationBuilder().build(em, 20);
//        new UserBuilder().build(em, 10);
//        new DynamoDbSampleBuilder().build(em, 25);
//        new DatastoreBuilder().build(em, 4);
//        new DataFlowBuilder().build(em, 5);

        Instant startDate = Instant.parse(args[0]);
        Instant endDate = Instant.parse(args[1]);
        new JobBuilder().build(emf, startDate, endDate);

        em.close();
        emf.close();
        log.info("Build complete.");
    }
}
