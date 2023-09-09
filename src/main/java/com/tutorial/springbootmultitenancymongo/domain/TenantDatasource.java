package com.tutorial.springbootmultitenancymongo.domain;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.tutorial.springbootmultitenancymongo.service.EncryptionService;
import com.tutorial.springbootmultitenancymongo.service.EncryptionServiceImpl;
import com.tutorial.springbootmultitenancymongo.service.MongoDataSourceService;
import lombok.*;

import java.util.Collections;

/**
 * <h2>TenantDatasource</h2>
 *
 * <p>
 * Description: this class is model for tenant datasource.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TenantDatasource {

    private String alias;
    private String host;
    private int port;
    private String database;
    private MongoClient client;


    public TenantDatasource(String host, String database)
    {
        this.host = host;
        this.database = database;
        createClient();
    }


    /**
     * Init mongo client
     */
    private void createClient()
    {

        client = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(host))
                        .build());
    }
}
