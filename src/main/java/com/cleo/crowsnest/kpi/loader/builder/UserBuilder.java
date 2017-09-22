package com.cleo.crowsnest.kpi.loader.builder;

import com.cleo.crowsnest.kpi.loader.entities.OrganizationDimension;
import com.cleo.crowsnest.kpi.loader.entities.UserDimension;

import java.text.DecimalFormat;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.extern.slf4j.Slf4j;

import static com.cleo.crowsnest.kpi.loader.builder.Monikers.getRandomName;

@Slf4j
public class UserBuilder {

    public void build(EntityManager em, int numToBuild) {
        TypedQuery<OrganizationDimension> orgQuery = em.createQuery("SELECT o FROM OrganizationDimension o", OrganizationDimension.class);
        orgQuery.getResultList().forEach(o -> {
            em.getTransaction().begin();
            for (int i = 1; i <= numToBuild; i++) {
                UserDimension dimension = new UserDimension();
                dimension.setId(UUID.randomUUID().toString());
                String name = getRandomName();
                String[] split = name.split("_");
                dimension.setFirstName(split[0]);
                dimension.setLastName(split[1]);
                dimension.setEmail(name + "@" + o.getName() + ".com");
                dimension.setOrganizationId(o.getId());
                em.persist(dimension);
                log.info("Created user: {}.", name);
            }
            em.getTransaction().commit();
        });
    }
}
