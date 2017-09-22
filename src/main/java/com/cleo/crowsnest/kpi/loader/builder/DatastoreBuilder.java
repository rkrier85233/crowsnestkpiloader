package com.cleo.crowsnest.kpi.loader.builder;

import com.cleo.crowsnest.kpi.loader.entities.AccessPointDimension;
import com.cleo.crowsnest.kpi.loader.entities.DatastoreDimension;
import com.cleo.crowsnest.kpi.loader.entities.OrganizationDimension;
import com.cleo.crowsnest.kpi.loader.entities.UserDimension;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.extern.slf4j.Slf4j;

import static com.cleo.crowsnest.kpi.loader.builder.Monikers.getRandomName;

@Slf4j
public class DatastoreBuilder {

    public void build(EntityManager em, int numToBuild) {
        DecimalFormat fmt = new DecimalFormat("0000");
        TypedQuery<AccessPointDimension> apQuery = em.createQuery("SELECT a FROM AccessPointDimension a", AccessPointDimension.class);
        apQuery.getResultList().forEach(a -> {
            em.getTransaction().begin();
            for (int i = 1; i <= numToBuild; i++) {
                DatastoreDimension dimension = new DatastoreDimension();
                dimension.setId(UUID.randomUUID().toString());
                dimension.setName(getRandomName());
                dimension.setPath(String.format("/from/%s/path/%s", dimension.getName(), fmt.format(i)));
                dimension.setOrganizationId(a.getOrganizationId());
                dimension.setAccessPointId(a.getId());
                em.persist(dimension);
                log.info("Created data store: {}.", dimension.getName());
            }
            em.getTransaction().commit();
        });
    }
}
