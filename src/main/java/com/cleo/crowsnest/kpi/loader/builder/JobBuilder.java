package com.cleo.crowsnest.kpi.loader.builder;

import com.cleo.crowsnest.kpi.loader.entities.AccessPointDimension;
import com.cleo.crowsnest.kpi.loader.entities.DataFlowDimension;
import com.cleo.crowsnest.kpi.loader.entities.DatastoreDimension;
import com.cleo.crowsnest.kpi.loader.entities.JobFact;
import com.cleo.crowsnest.kpi.loader.entities.OrganizationDimension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPOutputStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobBuilder {

    public void build(EntityManagerFactory emf, Instant startDate, Instant endDate) throws IOException {
        String s3Path = "/Users/rkrier/job-fact";
        EntityManager em = emf.createEntityManager();
        TypedQuery<OrganizationDimension> orgQuery = em.createQuery("SELECT o FROM OrganizationDimension o", OrganizationDimension.class);
        List<OrganizationDimension> organizations = orgQuery.getResultList();
        ExecutorService executor = Executors.newFixedThreadPool(organizations.size());
        List<Future<Void>> futures = new ArrayList<>();
        organizations.forEach(o -> {
            futures.add(executor.submit(new JobBuilderTask(emf, s3Path, o, startDate, endDate)));
        });
        em.close();

        futures.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException e) {
                log.error("Interrupted, cause: {}.", e, e);
            } catch (ExecutionException e) {
                log.error("Thread failure building jobs, cause: {}.", e, e);
            }
        });

        executor.shutdown();
    }

    private static class JobBuilderTask implements Callable<Void> {
        private static final long ONE_MEG = (long)Math.pow(1024, 2);
        private static final long ONE_GIG = (long)Math.pow(1024, 3);
        private static final double MIN_BYTES_PER_SECOND = 3 * ONE_MEG;
        private static final double MAX_BYTES_PER_SECOND = 50 * ONE_MEG;
        private static final int MIN_NUMBER_FILES = 5;
        private static final int MAX_NUMBER_FILES = 15;
        private static final long MIN_FILE_SIZE = 512;
        private static final long MAX_FILE_SIZE = 5 * ONE_GIG;

        private HashMap<String, AccessPointDimension> accessPointCache = new HashMap<>();
        private HashMap<String, DatastoreDimension> datastoreCache = new HashMap<>();

        private EntityManagerFactory emf;
        private String outDir;
        private OrganizationDimension organization;
        private Instant startDate;
        private Instant endDate;

        JobBuilderTask(EntityManagerFactory emf, String outDir, OrganizationDimension organization, Instant startDate, Instant endDate) {
            this.emf = emf;
            this.outDir = outDir;
            this.organization = organization;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        public Void call() throws Exception {
            ThreadLocalRandom random = ThreadLocalRandom.current();

            String gzipFileName = String.format("jobfact-%s-%s.tbl.gz", organization.getName(), endDate.toString());
            File gzipFile = new File(outDir, gzipFileName);
            gzipFile.getParentFile().mkdirs();

            GZIPOutputStream gzipOS = new GZIPOutputStream(new FileOutputStream(gzipFile));
            EntityManager em = emf.createEntityManager();
            TypedQuery<DataFlowDimension> dfQuery = em.createQuery("SELECT d FROM DataFlowDimension d WHERE d.organizationId = :orgId", DataFlowDimension.class);
            dfQuery.setParameter("orgId", organization.getId());
            dfQuery.getResultList().forEach(df -> {
                long jobsPerDataflow = 0;
                double failurePercent = (1d + random.nextInt(2)) / 100;
                Instant startDate = this.startDate;
                while (true) {
                    jobsPerDataflow++;
                    String status = "SUCCESS";
                    if ((jobsPerDataflow * failurePercent) % 1 == 0) {
                        status = "FAILURE";
                    }
                    JobFact jobFact = createJobFact(em, df, status, startDate);
                    try {
                        gzipOS.write((jobFact.toString() +"\n").getBytes(Charset.forName("UTF-8")));
                    } catch (IOException e) {
                        log.error("Unable to write job fact, cause: {}", e, e);
                        break;
                    }
                    log.info("Created job for data flow: {} {}, endDate: {}.", organization.getName(), df.getName(), jobFact.getEndDate());
                    long offset = df.getInterval() * 60 * 1000;
                    if (jobFact.getEndDateEpoch() > endDate.toEpochMilli()) {
                        break;
                    }
                    startDate = startDate.plusMillis(offset);
                }
            });
            em.close();
            gzipOS.close();
            return null;
        }

        private JobFact createJobFact(EntityManager em, DataFlowDimension dataFlow, String status, Instant startDate) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            JobFact jobFact = new JobFact();
            jobFact.setId(UUID.randomUUID().toString());
            DatastoreDimension srcDatastore = getDatastore(em, dataFlow.getSourceDatastoreId());
            DatastoreDimension destDatastore = getDatastore(em, dataFlow.getDestinationDatastoreId());
            AccessPointDimension srcAccessPoint = getAccessPoint(em, srcDatastore.getAccessPointId());
            AccessPointDimension destAccessPoint = getAccessPoint(em, destDatastore.getAccessPointId());
            jobFact.setSourceAccessPointId(srcAccessPoint.getId());
            jobFact.setSourceDatastoreId(srcDatastore.getId());
            jobFact.setDestAccessPointId(destAccessPoint.getId());
            jobFact.setDestDatastoreId(destDatastore.getId());
            jobFact.setUserId(srcAccessPoint.getUserId());
            jobFact.setOrganizationId(srcAccessPoint.getOrganizationId());
            jobFact.setDataflowId(dataFlow.getId());

            Integer numItems = MIN_NUMBER_FILES + random.nextInt(MAX_NUMBER_FILES);
            Long bytesTransferred = numItems * (MIN_FILE_SIZE + random.nextLong(MAX_FILE_SIZE));
            Double bytesPerSecond = MIN_BYTES_PER_SECOND + random.nextDouble(MAX_BYTES_PER_SECOND);
            Double throughPut = bytesTransferred / bytesPerSecond;
            Long duration = new Double(numItems * throughPut).longValue();
            Instant endDate = startDate.plusMillis(duration + 50);

            jobFact.setStartDate(Date.from(startDate));
            jobFact.setEndDate(Date.from(endDate));
            jobFact.setStatus(status);
            jobFact.setDuration(duration);
            jobFact.setNumberItems(numItems);
            jobFact.setBytesTransferred(bytesTransferred);
            jobFact.setThroughput(throughPut);

            return jobFact;
        }

        private AccessPointDimension getAccessPoint(EntityManager em, String id) {
            return accessPointCache.computeIfAbsent(id, v -> em.find(AccessPointDimension.class, id));
        }

        private DatastoreDimension getDatastore(EntityManager em, String id) {
            return datastoreCache.computeIfAbsent(id, v -> em.find(DatastoreDimension.class, id));
        }
    }
}
