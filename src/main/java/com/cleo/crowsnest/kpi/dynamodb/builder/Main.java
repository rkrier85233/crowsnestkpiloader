package com.cleo.crowsnest.kpi.dynamodb.builder;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    private static final String DYNAMODB_ENDPOINT = "https://dynamodb.us-west-2.amazonaws.com";

    public static void main(String[] args) throws IOException {
        Instant startDate = Instant.parse(args[0]);
        Instant endDate = Instant.parse(args[1]);

        AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard();
        builder.setCredentials(DefaultAWSCredentialsProviderChain.getInstance());
        builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(DYNAMODB_ENDPOINT, null));
        AmazonDynamoDB amazonDynamoDB = builder.build();
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB, createConfig());

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("redshift-kpi");
        EntityManager em = emf.createEntityManager();

        File file = new File("src/main/resources/user_attributues.json");
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map> userAttributesList = Arrays.asList(objectMapper.readValue(file, Map[].class));
        Map<String, Object> map = userAttributesList.stream().filter(m -> m.get("name").equals("rkrier@cleo.com")).findFirst().orElse(null);
        Map<String, String> userAttributes = (Map<String, String>) map.get("attributes");

        DynamoDbSampleBuilder sampleBuilder = new DynamoDbSampleBuilder();
        sampleBuilder.buildAccessPoints(em, mapper, startDate, endDate, userAttributes);
        sampleBuilder.buildDataFlows(em, mapper, startDate, endDate, userAttributes);
        em.close();
        emf.close();
        log.info("Build complete.");
    }

    private static DynamoDBMapperConfig createConfig() {
        String prefix = System.getProperty("amazon.dynamodb.table.prefix");
        if (prefix == null) {
            return DynamoDBMapperConfig.DEFAULT;
        }
        prefix = prefix.endsWith(".") ? prefix : prefix.concat(".");
        return
                new DynamoDBMapperConfig.Builder().withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(prefix))
                        .build();
    }
}
