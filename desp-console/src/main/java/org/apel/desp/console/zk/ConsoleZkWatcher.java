package org.apel.desp.console.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.consist.ZKNodePath;
import org.apel.desp.commons.domain.ZKCommand;
import org.apel.desp.commons.monitor.AgentMonitorInfo;
import org.apel.desp.commons.util.NetUtil;
import org.apel.desp.commons.util.ZKConnector;
import org.apel.desp.console.service.AppInstanceService;
import org.apel.desp.console.service.CommandService;
import org.apel.desp.console.service.MachineInstanceService;
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
	@Autowired
	private MachineInstanceService machineInstanceService;
	@Autowired
	private AppInstanceService appInstanceService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//将所有物理机实例的运行状态清空
		machineInstanceService.clearAllStatus();
		
		//监听agents变化，同步更新数据库
		watchAgentChange();
		
		//监听commands节点的变化，如有更新变化，则同步更新数据库
		watchCommandChange();
	}

	@SuppressWarnings("resource")
	private void watchAgentChange() {
		CuratorFramework client = zkConnector.getClient();
		PathChildrenCache agentsChildrenCache = new PathChildrenCache(client,
				ZKNodePath.ZK_ACTIVE_AGENTS_PATH, false);
		try {
			agentsChildrenCache.start();
			agentsChildrenCache.getListenable()
			.addListener(new PathChildrenCacheListener() {
				@Override
				public void childEvent(CuratorFramework arg0,
						PathChildrenCacheEvent event) throws Exception {
					String macAddress  = "";
					if (event.getData() != null){
						macAddress = NetUtil.macPureToRaw(ZKNodePath.getLeafNodeName(event.getData().getPath()));
					}
					switch (event.getType()) {
					case CHILD_ADDED://监听agents节点个数变化，如果有新增，则更新物理机实例的状态为运行中
						
						machineInstanceService.updateStatus(macAddress, SystemConsist.AGENT_STATUS_RUNNING, getAgentMonitorInfo(event).getAgentVersion());//更新物理机状态
						appInstanceService.updateStatus(macAddress, getAgentMonitorInfo(event).getApps());//更新物理机上的app状态
						break;
					case CHILD_UPDATED://监听agent节点的状态变化，收到变化通知，更新本地数据库
						
						appInstanceService.updateStatus(macAddress, getAgentMonitorInfo(event).getApps());//更新物理机上的app状态
						break;
					case CHILD_REMOVED://监听agents节点个数变化，如果有新增，则更新物理机实例的状态会已停止
						
						machineInstanceService.updateStatus(macAddress, SystemConsist.AGENT_STATUS_STOPED, "none");
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
	
	private AgentMonitorInfo getAgentMonitorInfo(PathChildrenCacheEvent event){
		AgentMonitorInfo monitorInfo = null;
		try {
			byte[] dataBytes = zkConnector.getClient().getData().forPath(event.getData().getPath());
			monitorInfo = JSON.parseObject(new String(dataBytes), AgentMonitorInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
		return monitorInfo;
	}

	/**
	 * 
	 */
	@SuppressWarnings("resource")
	private void watchCommandChange() {
		CuratorFramework client = zkConnector.getClient();
		TreeCache commandChildrenCache = new TreeCache(client, ZKNodePath.ZK_COMMONDS_PATH);
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
