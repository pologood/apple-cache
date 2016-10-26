package com.appleframework.cache.j2cache.spring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.redisson.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import net.sf.ehcache.CacheManager;

public class SpringJ2CacheManager extends AbstractCacheManager {

	private ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();
	private Map<String, Integer> expireMap = new HashMap<String, Integer>();
	private Map<String, Boolean> openMap = new HashMap<String, Boolean>();

	private RedissonClient redisson;
	private CacheManager ehcacheManager;

	public SpringJ2CacheManager() {
	}

	@Override
	protected Collection<? extends Cache> loadCaches() {
		Collection<Cache> values = cacheMap.values();
		return values;
	}

	@Override
	public Cache getCache(String name) {
		Cache cache = cacheMap.get(name);
		if (cache == null) {
			Integer expire = expireMap.get(name);
			if (expire == null) {
				expire = 0;
				expireMap.put(name, expire);
			}
			Boolean isOpen = openMap.get(name);
			if (isOpen == null) {
				isOpen = true;
				openMap.put(name, isOpen);
			}
			if(!isOpen) {
				cache = new SpringJ2Cache(ehcacheManager, redisson, name, expire.intValue(), false);
			}
			else {
				cache = new SpringJ2Cache(ehcacheManager, redisson, name, expire.intValue());
			}
			cacheMap.put(name, cache);
		}
		return cache;
	}

	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}

	public void setEhcacheManager(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}

	public void setExpireConfig(Map<String, Integer> expireConfig) {
		this.expireMap = expireConfig;
	}

	public void setOpenConfig(Map<String, Boolean> openConfig) {
		this.openMap = openConfig;
	}

}