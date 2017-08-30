package org.apel.desp.agent.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

/**
 * zk命令线程池，每一个应用都对应一个固定的线程池，命令执行的时候只能穿行一个一个的执行
 * @author lijian
 *
 */
@Component
public class ZKCommandPool {

	//键为appId，值为对应的pool
	private Map<String, ExecutorService> commandPoolMap = new ConcurrentHashMap<>();
	
	public ExecutorService getPoolOrSet(String appId){
		if (getPool(appId) == null){
			addPool(appId);
		}
		return getPool(appId);
	}
	
	public ExecutorService getPool(String appId){
		return commandPoolMap.get(appId);
	}
	
	public void addPool(String appId){
		commandPoolMap.put(appId, Executors.newFixedThreadPool(1));
	}
	
	public void del(String appId){
		commandPoolMap.remove(appId);
	}
	
	public Map<String, ExecutorService> getRaw(){
		return commandPoolMap;
	}
	
	
}
