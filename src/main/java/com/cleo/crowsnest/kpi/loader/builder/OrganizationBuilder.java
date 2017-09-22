package com.cleo.crowsnest.kpi.loader.builder;

import com.cleo.crowsnest.kpi.loader.entities.OrganizationDimension;

import java.text.DecimalFormat;
import java.util.UUID;

import javax.persistence.EntityManager;

import lombok.extern.slf4j.Slf4j;

import static com.cleo.crowsnest.kpi.loader.builder.Monikers.getRandomName;

@Slf4j
public class OrganizationBuilder {

    public void build(EntityManager em, int numToBuild) {
        DecimalFormat fmt = new DecimalFormat("0000");
        em.getTransaction().begin();
        for (int i = 1; i <= numToBuild; i++) {
            OrganizationDimension dimension = new OrganizationDimension();
            dimension.setId(UUID.randomUUID().toString());
            dimension.setName(getRandomName());
            dimension.setPoolName("Pool-" + fmt.format(i));
            em.persist(dimension);
            log.info("Created organization: {}.", dimension.getName());
        }
        em.getTransaction().commit();
    }
}
