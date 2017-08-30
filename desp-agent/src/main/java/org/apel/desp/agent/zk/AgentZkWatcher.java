package org.apel.desp.agent.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apel.desp.commons.consist.ZKNodePath;
import org.apel.desp.commons.util.NetUtil;
import org.apel.desp.commons.util.ZKConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;

/**
 * agent zookeeper watcher 绑定zookeeper节点，根据节点变化进行业务逻辑
 * 
 * @author lijian
 *
 */
@Component
public class AgentZkWatcher implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private ZKConnector zkConnector;

	@SuppressWarnings("resource")
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		CuratorFramework client = zkConnector.getClient();
		
		try {
			String agentPath = ZKNodePath.ZK_ACTIVE_AGENTS_PATH + "/" + NetUtil.getLocalPureMac();
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(agentPath);
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
		
		try {
			PathChildrenCache childrenCache = new PathChildrenCache(client,
					ZKNodePath.ZK_COMMONDS_PATH, false);
			childrenCache.start();
			childrenCache.getListenable()
			.addListener(new PathChildrenCacheListener() {
				@Override
				public void childEvent(CuratorFramework arg0,
						PathChildrenCacheEvent event) throws Exception {
					System.out.println("change");
					switch (event.getType()) {
					case CHILD_ADDED:
						System.out.println("子元素增加");
						String leftNodeName = ZKNodePath.getLeafNodeName(event.getData().getPath());
						if (leftNodeName.equals(NetUtil.getLocalPureMac())){//判断增加的命令节点是否是本agent
							byte[] forPath = client.getData().forPath(event.getData().getPath());
							System.out.println(new String(forPath));
						}
						break;
					case CHILD_UPDATED:
						if (event.getData().getPath().equals(NetUtil.getLocalPureMac())){
							System.out.println(event.getData().getData());
						}
						System.out.println("子元素修改");
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
	}

}
