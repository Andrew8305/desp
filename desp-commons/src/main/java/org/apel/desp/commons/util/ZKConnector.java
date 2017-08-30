package org.apel.desp.commons.util;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZKConnector implements InitializingBean{

	private final static Logger LOG = LoggerFactory.getLogger(ZKConnector.class);
	
	private CuratorFramework client;
	
	@Value("${desp.zookeeper.address:127.0.0.1:2181}")
	private String address;
	@Value("${desp.zookeeper.sessionTimeoutMs:5000}")
	private int sessionTimeoutMs;
	@Value("${desp.zookeeper.connectionTimeoutMs:3000}")
	private int connectionTimeoutMs;
	@Value("${desp.zookeeper.retryBaseSleepTimeMs :3}")
	private int retryBaseSleepTimeMs;
	@Value("${desp.zookeeper.maxRetries:3}")
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
		 LOG.info("连接zookeeper成功, 地址：" + address);
	}
	
	
}

