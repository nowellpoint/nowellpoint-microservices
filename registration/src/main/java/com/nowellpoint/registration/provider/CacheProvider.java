package com.nowellpoint.registration.provider;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import com.nowellpoint.registration.util.EnvironmentVariables;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

@Startup
@Singleton
public class CacheProvider {
	
	@Inject
	private Logger logger;
	
	private JedisPool jedisPool;
	
	public Jedis getCache() {
		return jedisPool.getResource();
	}
	
	@PostConstruct
	public void init() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1000);
		
		jedisPool = new JedisPool(
				poolConfig, 
				EnvironmentVariables.getRedisHost(), 
				EnvironmentVariables.getRedisPort(), 
				Protocol.DEFAULT_TIMEOUT, 
				EnvironmentVariables.getRedisPassword());
		
		logger.info("connecting to cache...is connected: " + ! jedisPool.isClosed());
	}
	
	@PreDestroy
	public void destroy() {
		try {
			jedisPool.destroy();
        } catch (Exception e) {
        	    logger.warn(String.format("Cannot properly close Jedis pool %s", e.getMessage()));
        }
		
		logger.info("disconnecting from cache...is connected: " + ! jedisPool.isClosed());
		
		jedisPool = null;
	}
}