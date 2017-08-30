package org.apel.desp.commons.util;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZKConnector implements InitializingBean{

	private CuratorFramework client;
	
	@Value("${zookeeper.address:127.0.0.1:2181}")
	private String address;
	@Value("${zookeeper.sessionTimeoutMs:5000}")
	private int sessionTimeoutMs;
	@Value("${zookeeper.connectionTimeoutMs:3000}")
	private int connectionTimeoutMs;
	@Value("${zookeeper.retryBaseSleepTimeMs :3}")
	private int retryBaseSleepTimeMs;
	@Value("${zookeeper.maxRetries:3}")
	private int maxRetries;

	public void close(){
		client.close();
	}
	
	public CuratorFramework getClient(){
		return client;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		 RetryPolicy retryPolicy = new ExponentialBackoffRetry(retryBaseSleepTimeMs, maxRetries);
		 client = CuratorFrameworkFactory.builder()
		 					.connectString(address)
		 					.sessionTimeoutMs(sessionTimeoutMs)
		 					.connectionTimeoutMs(connectionTimeoutMs)
		 					.retryPolicy(retryPolicy)
		 					.build();
		 client.start();
	}
	
	
}

