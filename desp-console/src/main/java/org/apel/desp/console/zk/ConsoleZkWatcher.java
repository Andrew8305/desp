package org.apel.desp.console.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apel.desp.commons.consist.ZKNodePath;
import org.apel.desp.commons.util.ZKConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;

/**
 * 
 * console zookeeper watcher
 * 绑定zookeeper节点，根据节点变化进行业务逻辑
 * @author lijian
 *
 */
@Component
public class ConsoleZkWatcher implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private ZKConnector zkConnector;
	
	@SuppressWarnings("resource")
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		CuratorFramework client = zkConnector.getClient();
		PathChildrenCache childrenCache = new PathChildrenCache(client,
				ZKNodePath.ZK_ACTIVE_AGENTS_PATH, false);
		try {
			childrenCache.start();
			childrenCache.getListenable()
			.addListener(new PathChildrenCacheListener() {
				@Override
				public void childEvent(CuratorFramework arg0,
						PathChildrenCacheEvent event) throws Exception {
					System.out.println(123);
					switch (event.getType()) {
					case CHILD_ADDED:
						String leftNodeName = ZKNodePath.getLeafNodeName(event.getData().getPath());
						String path = ZKNodePath.ZK_COMMONDS_PATH + "/" + leftNodeName;
						if (client.checkExists().forPath(path) == null){//如果节点不存在
							client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "start".getBytes());
						}
						System.out.println("子元素增加");
						break;
					case CHILD_UPDATED:
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
