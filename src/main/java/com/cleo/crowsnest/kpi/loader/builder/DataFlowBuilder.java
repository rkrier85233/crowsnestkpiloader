package com.cleo.crowsnest.kpi.loader.builder;

import com.cleo.crowsnest.kpi.loader.entities.AccessPointDimension;
import com.cleo.crowsnest.kpi.loader.entities.DataFlowDimension;
import com.cleo.crowsnest.kpi.loader.entities.DatastoreDimension;
import com.cleo.crowsnest.kpi.loader.entities.OrganizationDimension;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.extern.slf4j.Slf4j;

import static com.cleo.crowsnest.kpi.loader.builder.Monikers.getRandomName;

@Slf4j
public class DataFlowBuilder {
    private static String[] OPERATIONS = {"MOVE", "COPY"};

    public void build(EntityManager em, int numToBuild) {
        Random random = new Random();
        TypedQuery<OrganizationDimension> orgQuery = em.createQuery("SELECT o FROM OrganizationDimension o", OrganizationDimension.class);
        List<Thread> threads = new ArrayList<>();
        orgQuery.getResultList().forEach(o -> {
            threads.add(new Thread(() -> {
                EntityManager em2 = em.getEntityManagerFactory().createEntityManager();
                TypedQuery<DatastoreDimension> dsQuery = em2.createQuery("SELECT d FROM DatastoreDimension d WHERE d.organizationId = :orgId", DatastoreDimension.class);
                dsQuery.setParameter("orgId", o.getId());
                List<DatastoreDimension> datastores = dsQuery.getResultList();
                datastores.forEach(src -> {
                    em2.getTransaction().begin();
                    for (int i = 1; i <= numToBuild; i++) {
                        DatastoreDimension dest = getDestination(random, src, datastores);
                        DataFlowDimension dimension = new DataFlowDimension();
                        dimension.setId(UUID.randomUUID().toString());
                        dimension.setName(String.format("%s -> %s", src.getName(), dest.getName()));
                        dimension.setScheduled(true);
                        dimension.setInterval(5 + random.nextInt(10));
                        dimension.setTimeUnit("MINUTES");
                        dimension.setOperation(OPERATIONS[random.nextInt(OPERATIONS.length)]);
                        dimension.setOrganizationId(o.getId());
                        dimension.setSourceDatastoreId(src.getId());
                        dimension.setDestinationDatastoreId(dest.getId());
                        em2.persist(dimension);
                        log.info("Created data flow: {}.", dimension.getName());
                    }
                    em2.getTransaction().commit();
                });
                em2.close();
                log.info("Finished creating data flows for organization: {}.", o.getName());
            }, o.getName()));
        });

        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                log.info("Waiting for data flow creation thread for organization: {} to finish.", t.getName());
                t.join();
                log.info("Data flow creation thread for organization: {} finished.", t.getName());
            } catch (InterruptedException e) {
                log.info("Data flow creation thread for organization: {} was interrupted, cause: {}", t.getName(), e, e);
            }
        });
    }

    private DatastoreDimension getDestination(Random random, DatastoreDimension src, List<DatastoreDimension> datastores) {
        DatastoreDimension dest = datastores.get(random.nextInt(datastores.size()));
        while (dest.getId().equals(src.getId())) {
            dest = datastores.get(random.nextInt(datastores.size()));
        }
        return dest;
    }
}
