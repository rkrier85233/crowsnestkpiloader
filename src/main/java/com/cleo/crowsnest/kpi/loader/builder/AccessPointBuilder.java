package com.cleo.crowsnest.kpi.loader.builder;

import com.cleo.crowsnest.kpi.loader.entities.AccessPointDimension;
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
public class AccessPointBuilder {

    private static String[] PLATFORMS = {"CENTOS", "UBUNTU", "RED_HAT", "WINDOWS"};

    public void build(EntityManager em, int numToBuild) {
        Random random = new Random();
        TypedQuery<OrganizationDimension> orgQuery = em.createQuery("SELECT o FROM OrganizationDimension o", OrganizationDimension.class);
        orgQuery.getResultList().forEach(o -> {
            TypedQuery<UserDimension> userQuery = em.createQuery("SELECT u FROM UserDimension u WHERE u.organizationId = :orgId", UserDimension.class);
            userQuery.setParameter("orgId", o.getId());
            List<UserDimension> users = userQuery.getResultList();

            em.getTransaction().begin();
            for (int i = 1; i <= numToBuild; i++) {
                UserDimension user = users.get(random.nextInt(users.size()));
                AccessPointDimension dimension = new AccessPointDimension();
                dimension.setId(UUID.randomUUID().toString());
                dimension.setName(getRandomName());
                dimension.setPlatform(PLATFORMS[random.nextInt(PLATFORMS.length)]);
                dimension.setTransportUri(String.format("https://%s.compute.amazonaws.com:17443/", dimension.getName()));
                dimension.setOrganizationId(o.getId());
                dimension.setUserId(user.getId());
                em.persist(dimension);
                log.info("Created access point: {}.", dimension.getName());
            }
            em.getTransaction().commit();
        });
    }
}
