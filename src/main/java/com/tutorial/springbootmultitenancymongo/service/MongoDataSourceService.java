package com.tutorial.springbootmultitenancymongo.service;

import com.mongodb.client.MongoClient;
import com.tutorial.springbootmultitenancymongo.DTO.TenantDetails;
import com.tutorial.springbootmultitenancymongo.configuration.ApplicationProperties;
import com.tutorial.springbootmultitenancymongo.domain.TenantDatasource;
import org.springframework.stereotype.Service;


/**
 * <h2>RedisDatasourceService</h2>
 * This class is used to store all information from tenant databases.
 */
@Service
public class MongoDataSourceService
{
    private final ApplicationProperties applicationProperties;
    private final EncryptionService encryptionService;

    public MongoDataSourceService(ApplicationProperties applicationProperties, EncryptionService encryptionService)
    {
        this.applicationProperties = applicationProperties;
        this.encryptionService = encryptionService;
    }

    public TenantDatasource createMongoClientForTenant(String tenantId, MongoClient defaultClient)
    {
        TenantDetails tenantDetails = PerformMongoQuery.findTenantSrv(tenantId, defaultClient);
        String decryptedSrv =  encryptionService.decrypt(tenantDetails.getSrv(),
                applicationProperties.getEncryption().getSecret(),
                applicationProperties.getEncryption().getSalt());
        return new TenantDatasource(decryptedSrv, tenantDetails.getDatabase());
    }
}
