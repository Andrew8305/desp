package org.apel.desp.agent.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.apel.desp.agent.command.DefaultZKCommandCallback;
import org.apel.desp.agent.command.ZKCommandManager;
import org.apel.desp.agent.util.SigarUtil;
import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.consist.ZKNodePath;
import org.apel.desp.commons.domain.ZKCommand;
import org.apel.desp.commons.monitor.AgentMonitorInfo;
import org.apel.desp.commons.util.NetUtil;
import org.apel.desp.commons.util.ZKConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;

/**
 * agent zookeeper watcher 绑定zookeeper节点，根据节点变化进行业务逻辑
 * 
 * @author lijian
 *
 */
@Component
public class AgentZkWatcher implements ApplicationListener<ContextRefreshedEvent> {

	private static Logger LOG = LoggerFactory.getLogger(AgentZkWatcher.class);
	
	@Autowired
	private ZKConnector zkConnector;
	@Autowired
	private ZKCommandManager zkCommandManager;
	@Value("${agentVersion:none}")
	private String agentVersion;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//发送agent的基础信息到zk，代表agent已经启动
		sendAgentStatus();
		
		//监听根节点，在重连时，建立agent节点
		watchRootChange();
		
		//监听console向agent发出的命令(来一个命令执行一个命令)
		watchCommandsChange();
	}
	
	@SuppressWarnings("resource")
	private void watchRootChange(){
		try {
			CuratorFramework client = zkConnector.getClient();
			PathChildrenCache childrenCache = new PathChildrenCache(client,
					ZKNodePath.ZK_ROOT_PATH, false);
			childrenCache.start();
			childrenCache.getListenable()
			.addListener(new PathChildrenCacheListener() {
				@Override
				public void childEvent(CuratorFramework arg0,
						PathChildrenCacheEvent event) throws Exception {
					switch (event.getType()) {
					case CONNECTION_RECONNECTED://重连时重建agent临时节点
						Stat stat = client.checkExists().forPath(ZKNodePath.ZK_ACTIVE_AGENTS_PATH + "/" + NetUtil.getLocalPureMac());
						if (stat == null){
							sendAgentStatus();
						}
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
	
	@SuppressWarnings("resource")
	private void watchCommandsChange(){
		try {
			CuratorFramework client = zkConnector.getClient();
			PathChildrenCache childrenCache = new PathChildrenCache(client,
					ZKNodePath.ZK_COMMONDS_PATH + "/" + NetUtil.getLocalPureMac(), false);
			childrenCache.start();
			childrenCache.getListenable()
			.addListener(new PathChildrenCacheListener() {
				@Override
				public void childEvent(CuratorFramework arg0,
						PathChildrenCacheEvent event) throws Exception {
					switch (event.getType()) {
					case CHILD_ADDED://监听console发送的命令，接收命令并执行，命令执行完成之后告诉zk命令执行完成
						byte[] forPath = client.getData().forPath(event.getData().getPath());
						String data = new String(forPath);
						LOG.info("获取到console发出的命令:" + data);
						ZKCommand zkCommand = JSON.parseObject(data, ZKCommand.class);
						zkCommandManager.execute(zkCommand, new DefaultZKCommandCallback(client, zkCommand, event));
						break;
					case CHILD_UPDATED:
						break;
					case CHILD_REMOVED:
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
	
	private void sendAgentStatus(){
		CuratorFramework client = zkConnector.getClient();
		AgentMonitorInfo agentMonitorInfo = new AgentMonitorInfo();
		agentMonitorInfo.setStatus(SystemConsist.AGENT_STATUS_RUNNING);
		agentMonitorInfo.setAgentVersion(agentVersion);
		agentMonitorInfo.setCpus(SigarUtil.calculateCpuInfo());
		agentMonitorInfo.setMemory(SigarUtil.calculateMemory());
		String monitorInfo = JSON.toJSONString(agentMonitorInfo);
		try {
			String agentPath = ZKNodePath.ZK_ACTIVE_AGENTS_PATH + "/" + NetUtil.getLocalPureMac();
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(agentPath, monitorInfo.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
	}

}
