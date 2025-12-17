package com.creperie.gestion.infrastructure;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FixCache {

    /*
    java.util.NoSuchElementException: No value present
        at java.base/java.util.Optional.get(Optional.java:143)
        at io.quarkus.cache.runtime.CacheResultInterceptor.intercept(CacheResultInterceptor.java:48)
        at io.quarkus.cache.runtime.CacheResultInterceptor_Bean.intercept(Unknown Source)
     */
    // https://github.com/quarkusio/quarkus/issues/19676
    @Inject
    @CacheName("passphrase")
    Cache cache;
}
