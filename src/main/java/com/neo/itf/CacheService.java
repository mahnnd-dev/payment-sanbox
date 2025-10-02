package com.neo.itf;

import java.util.concurrent.ConcurrentMap;

public interface CacheService<T> {
    ConcurrentMap<String, T> getCache();

    void cacheDataSync();
}
