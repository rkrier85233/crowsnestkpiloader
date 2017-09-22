package com.cleo.crowsnest.kpi.loader.entities;

import java.time.Instant;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Test {

    public static void main(String[] args) {
        AccessPointDimension apDim = new AccessPointDimension();
        apDim.setId(UUID.randomUUID().toString());
        apDim.setName("Bob's Access Point");
        apDim.setPlatform("RED_HAT");
        apDim.setTransportUri("http://somewhere.com");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("redshift-kpi");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(apDim);
        em.getTransaction().commit();
        em.close();

        emf.close();

        System.out.println("Done");


    }
}
