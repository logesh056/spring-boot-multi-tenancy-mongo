package com.tutorial.springbootmultitenancymongo.exception;

/**
 * <h2>TenantAliasNotFoundException</h2>
 *
 * @author aek
 *
 * Description: trigger exception when tenant alias not found
 */
public class TenantNotFoundException extends RuntimeException {
    public TenantNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public TenantNotFoundException(String msg) {
        super(msg);
    }
}
