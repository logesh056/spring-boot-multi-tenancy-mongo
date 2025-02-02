package com.tutorial.springbootmultitenancymongo.filter;

import com.tutorial.springbootmultitenancymongo.exception.TenantNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

@Slf4j
@Component
public class TenantInterceptor
        implements WebRequestInterceptor
{

    private static final String TENANT_HEADER = "X-Tenant";
    private final TenantContext tenantContext;

    public TenantInterceptor(TenantContext tenantContext)
    {
        this.tenantContext = tenantContext;
    }

    @Override
    public void preHandle(WebRequest request)
    {
        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId != null && !tenantId.isEmpty()) {
            tenantContext.setTenantId(tenantId);
            log.info("Tenant header get: {}", tenantId);
        } else {
            log.error("Tenant header not found.");
            throw new TenantNotFoundException("Tenant header not found.");
        }
    }

    @Override
    public void postHandle(WebRequest webRequest, ModelMap modelMap) {
        tenantContext.setTenantId(null);
    }

    @Override
    public void afterCompletion(WebRequest webRequest, Exception e) {

    }
}