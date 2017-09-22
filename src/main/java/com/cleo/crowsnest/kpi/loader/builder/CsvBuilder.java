package com.cleo.crowsnest.kpi.loader.builder;

import com.cleo.crowsnest.kpi.loader.entities.AccessPointDimension;
import com.cleo.crowsnest.kpi.loader.entities.DataFlowDimension;
import com.cleo.crowsnest.kpi.loader.entities.DatastoreDimension;
import com.cleo.crowsnest.kpi.loader.entities.OrganizationDimension;
import com.cleo.crowsnest.kpi.loader.entities.UserDimension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Queue;
import java.util.zip.GZIPOutputStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvBuilder {

    public static void main(String[] args) throws IOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("redshift-kpi");
        EntityManager em = emf.createEntityManager();

        exportData(em, AccessPointDimension.class, "/Users/rkrier/temp/access-point.tbl.gz");
//        exportData(em, DataFlowDimension.class, "/Users/rkrier/temp/dataflow.tbl.gz");
//        exportData(em, DatastoreDimension.class, "/Users/rkrier/temp/datastore.tbl.gz");
//        exportData(em, OrganizationDimension.class, "/Users/rkrier/temp/organization.tbl.gz");
//        exportData(em, UserDimension.class, "/Users/rkrier/temp/user.tbl.gz");

        em.close();
        emf.close();
        log.info("Export complete.");
    }

    private static void exportData(EntityManager em, Class entity, String gzipFile) {
        File file = new File(gzipFile);
        file.getParentFile().mkdirs();
        Charset charset = Charset.forName("UTF-8");
        log.info("Exporting: {} to file: {}.", entity.getSimpleName(), gzipFile);
        try (GZIPOutputStream gzipOS = new GZIPOutputStream(new FileOutputStream(file))) {
            Query query = em.createQuery("SELECT x FROM " + entity.getName() + " x");
            query.getResultList().forEach(e -> {
                try {
                    gzipOS.write((e.toString() +"\n").getBytes(charset));
                } catch (IOException e1) {
                    log.error("Unable to write entity to gzip file: {}, cause: {}.", gzipFile, e, e);
                    return;
                }
            });
        } catch (IOException e) {
            log.error("Unable to create gzip file: {}, cause: {}.", gzipFile, e, e);
            throw new RuntimeException(e);
        }
    }
}
