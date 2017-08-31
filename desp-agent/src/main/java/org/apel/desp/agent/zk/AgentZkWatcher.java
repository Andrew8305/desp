package org.apel.desp.agent.zk;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apel.desp.agent.command.DefaultZKCommandCallback;
import org.apel.desp.agent.command.ZKCommandManager;
import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.consist.ZKNodePath;
import org.apel.desp.commons.domain.ZKCommand;
import org.apel.desp.commons.monitor.AgentMonitorInfo;
import org.apel.desp.commons.monitor.ApplicationMonitorInfo;
import org.apel.desp.commons.util.NetUtil;
import org.apel.desp.commons.util.ZKConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	@SuppressWarnings("resource")
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		CuratorFramework client = zkConnector.getClient();
		
		AgentMonitorInfo agentMonitorInfo = new AgentMonitorInfo();
		List<ApplicationMonitorInfo> apps = new ArrayList<>();
		ApplicationMonitorInfo applicationMonitorInfo = new ApplicationMonitorInfo();
		applicationMonitorInfo.setAppId("app2");
		applicationMonitorInfo.setStatus(SystemConsist.APPINSTANCE_STATUS_DEPLOYING);
		apps.add(applicationMonitorInfo);
		agentMonitorInfo.setApps(apps);
		String monitorInfo = JSON.toJSONString(agentMonitorInfo);
		try {
			String agentPath = ZKNodePath.ZK_ACTIVE_AGENTS_PATH + "/" + NetUtil.getLocalPureMac();
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(agentPath, monitorInfo.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
		
		try {
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
						System.out.println(new String(forPath));
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

}
