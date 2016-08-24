package com.thinkjoy.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shaojun on 8.24 024.
 */
public class CacheHandle {

    private static Map<String, Object> cacheMap = new HashMap<>();

    public static <T> void setCache(String key, T val) {
        cacheMap.put(key, val);
    }

    public static <T> T getCache(String key) {
        return (T) cacheMap.get(key);
    }
}
