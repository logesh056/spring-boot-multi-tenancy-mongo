package com.tutorial.springbootmultitenancymongo.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.tutorial.springbootmultitenancymongo.domain.TenantDatasource;
import com.tutorial.springbootmultitenancymongo.exception.TenantNotFoundException;
import com.tutorial.springbootmultitenancymongo.filter.TenantContext;
import com.tutorial.springbootmultitenancymongo.service.MongoDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MongoDataSources
{

    /**
     * Key: String tenant id
     * Value: TenantDatasource
     */
    private Map<String, TenantDatasource> tenantClients;
    private final ApplicationProperties applicationProperties;
    private final MongoDataSourceService mongoDataSourceService;
    private MongoClient defaultClient;
    private final TenantContext tenantContext;

    public MongoDataSources(ApplicationProperties applicationProperties, MongoDataSourceService mongoDataSourceService, TenantContext tenantContext)
    {
        this.applicationProperties = applicationProperties;
        this.mongoDataSourceService = mongoDataSourceService;
        this.tenantContext = tenantContext;
    }

    /**
     * Initialize all mongo datasource
     */
    @PostConstruct
    @Lazy
    public void initTenant()
    {
        tenantClients = new HashMap<>();
    }

    /**
     * Default Database name for spring initialization. It is used to be injected into the constructor of MultiTenantMongoDBFactory.
     *
     * @return String of default database.
     */
    @Bean
    public String databaseName()
    {
        return applicationProperties.getDatasourceDefault().getDatabase();
    }

    /**
     * Default Mongo Connection for spring initialization.
     * It is used to be injected into the constructor of MultiTenantMongoDBFactory.
     */
    @Bean
    public MongoClient getMongoClient()
    {
        defaultClient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(applicationProperties.getDatasourceDefault().getHost()))
                        .build());
        return defaultClient;
    }

    /**
     * This will get called for each DB operations
     *
     * @return MongoDatabase
     */
    public MongoDatabase mongoDatabaseCurrentTenantResolver() {
        try {
            final String tenantId = tenantContext.getTenantId();

            System.out.println("Total Tenants :");
            tenantClients.forEach((key, value) -> {
                System.out.println(key);
            });

            if (!tenantClients.containsKey(tenantId)) {
                tenantClients.put(tenantId, mongoDataSourceService.createMongoClientForTenant(tenantId, defaultClient));
            }

            return tenantClients.get(tenantId).getClient().
                    getDatabase(tenantClients.get(tenantId).getDatabase());
        } catch (NullPointerException exception) {
            throw new TenantNotFoundException("Tenant Datasource alias not found.");
        }
    }
}