package com.tutorial.springbootmultitenancymongo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.tutorial.springbootmultitenancymongo.DTO.TenantDetails;
import com.tutorial.springbootmultitenancymongo.exception.TenantNotFoundException;
import org.bson.Document;

import java.util.UnknownFormatConversionException;
import java.util.concurrent.atomic.AtomicReference;

public class PerformMongoQuery
{
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static TenantDetails findTenantSrv(String tenetId, MongoClient client) {
        MongoDatabase database = client.getDatabase("default");
        MongoCollection<Document> collection = database.getCollection("tenantsrv");

        Document query = new Document("tenantid", tenetId);
        AtomicReference<TenantDetails> tenantDetails = new AtomicReference<>();
        FindIterable<Document> documentList = collection.find(query);
        try (MongoCursor<Document> iterator = documentList.iterator())
        {
            if (iterator.hasNext()) {
            documentList.forEach(document -> {
                try {
                    System.out.println(document.toJson());
                    tenantDetails.set(objectMapper.readValue(document.toJson(), TenantDetails.class));
                } catch (JsonProcessingException e) {
                    throw new UnknownFormatConversionException("object conversion failed");
                }
            });
            documentList.iterator().close();
            return tenantDetails.get();
            }
            else {
            documentList.iterator().close();
            throw new TenantNotFoundException("Tenant id is invalid, not present in tenant details in db");
            }
        }
    }
}
