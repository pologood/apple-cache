package com.appleframework.cache.redis.spring;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.redisson.RedissonClient;
import org.redisson.core.RMapCache;

public class RedissonCacheOperation {

	private static Logger logger = Logger.getLogger(RedissonCacheOperation.class);

	private String name;
	private int expire = 0;
	
	private final RedissonClient redisson;
	
	public RMapCache<String, Object> getCacheMap() {
		return redisson.getMapCache(name);
	}

	public RedissonCacheOperation(RedissonClient redisson, String name, int expire) {
		this.name = name;
		this.expire = expire;
		this.redisson = redisson;
	}
	
	public RedissonCacheOperation(RedissonClient redisson, String name) {
		this.name = name;
		this.expire = 0;
		this.redisson = redisson;
	}

	public Object get(String key) {
		Object value = null;
		try {
			value = getCacheMap().get(key);
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
		return value;
	}

	public void put(String key, Object value) {
		if (value == null)
			return;
		try {
			if(expire == 0)
				getCacheMap().put(key, value);
			else
				getCacheMap().put(key, value, expire, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public void clear() {
		try {
			getCacheMap().clear();
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public void delete(String key) {
		try {
			getCacheMap().remove(key);
		} catch (Exception e) {
			logger.warn("cache error", e);
		}
	}

	public int getExpire() {
		return expire;
	}
	
}
