package org.apel.desp.console.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.consist.ZKNodePath;
import org.apel.desp.commons.domain.ZKCommand;
import org.apel.desp.commons.monitor.AgentMonitorInfo;
import org.apel.desp.commons.util.ZKConnector;
import org.apel.desp.console.service.CommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;

/**
 * 
 * console zookeeper watcher
 * 绑定zookeeper节点，根据节点变化进行业务逻辑
 * @author lijian
 *
 */
@Component
@Order(value = 1)
public class ConsoleZkWatcher implements ApplicationListener<ContextRefreshedEvent> {

	private static Logger LOG = LoggerFactory.getLogger(ConsoleZkWatcher.class);
	
	@Autowired
	private ZKConnector zkConnector;
	@Autowired
	private CommandService commandService;
	
	@SuppressWarnings("resource")
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		CuratorFramework client = zkConnector.getClient();
		
		PathChildrenCache agentsChildrenCache = new PathChildrenCache(client,
				ZKNodePath.ZK_ACTIVE_AGENTS_PATH, false);
		
		TreeCache commandChildrenCache = new TreeCache(client, ZKNodePath.ZK_COMMONDS_PATH);
		try {
			agentsChildrenCache.start();
			agentsChildrenCache.getListenable()
			.addListener(new PathChildrenCacheListener() {
				@Override
				public void childEvent(CuratorFramework arg0,
						PathChildrenCacheEvent event) throws Exception {
					switch (event.getType()) {
					case CHILD_ADDED:
						AgentMonitorInfo monitorInfo1 = JSON.parseObject(new String(client.getData().forPath(event.getData().getPath())), AgentMonitorInfo.class);
						System.out.println("一开始的时候:" + new String(client.getData().forPath(event.getData().getPath())));
						break;
					case CHILD_UPDATED:
						byte[] forPath = client.getData().forPath(event.getData().getPath());
						System.out.println("做了操作的时候:" + new String(forPath));
						AgentMonitorInfo monitorInfo = JSON.parseObject(new String(forPath), AgentMonitorInfo.class);
						System.out.println(monitorInfo);
						break;
					case CHILD_REMOVED:
						System.out.println("子元素删除");
						break;
					default:
						break;
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
		
		try {
			commandChildrenCache.start();
			commandChildrenCache.getListenable()
			.addListener(new TreeCacheListener() {
				@Override
				public void childEvent(CuratorFramework client, TreeCacheEvent event)
						throws Exception {
					switch (event.getType()) {
					case NODE_ADDED:
						break;
					case NODE_UPDATED:
						/*
						 * 监听agent对命令的修改，一旦修改了命令，则检查是否是命令执行完毕，
						 * 如果是，相应的修改数据库, 并且在变更数据库之后清楚zk命令
						 */
						String data = new String(client.getData().forPath(event.getData().getPath()));
						ZKCommand zkCommand = JSON.parseObject(data, ZKCommand.class);
						if (zkCommand.getStatus() == SystemConsist.COMMAND_EXE_STATUS_DONE){
							commandService.updateStatus(zkCommand.getId());
							client.delete().forPath(event.getData().getPath());
							LOG.info("命令执行完毕,更新数据库并删除zk节点:" + data);
						}
						break;
					case NODE_REMOVED:
						break;
					default:
						break;
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
	}
}
