package org.apel.desp.agent;

import java.util.concurrent.TimeUnit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;


public class Test2 {

	public static void main(String[] args) throws Exception {
		 RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		 CuratorFramework client = CuratorFrameworkFactory.builder()
		 					.connectString("127.0.0.1:2181")
		 					.sessionTimeoutMs(5000)
		 					.connectionTimeoutMs(3000)
		 					.retryPolicy(retryPolicy)
		 					.build();
		 client.start();
		 
//		 NodeCache nodeCache = new NodeCache(client, "/desp/activeAgents/", false);
//		 nodeCache.start();
//		 nodeCache.getListenable().addListener(new NodeCacheListener() {
//			
//			@Override
//			public void nodeChanged() throws Exception {
//				try {
//					System.out.println(new String(client.getData().forPath("/desp/status")));
//				} catch (NoNodeException e) {
//					System.out.println("没有节点");
//				}
//			}
//		});
		 
		 PathChildrenCache childrenCache = new PathChildrenCache(client, "/desp/activeAgents", false);
		 childrenCache.start();
		 childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
			
			@Override
			public void childEvent(CuratorFramework arg0, PathChildrenCacheEvent event)
					throws Exception {
				client.getChildren().forPath("/desp/activeAgents").forEach(t -> {
					System.out.println(t);
				});
				switch (event.getType()) {
				case CHILD_ADDED:
					System.out.println("子元素增加");
//					System.out.println(new String(client.getData().forPath("/desp/status")));
//					client.setData().forPath("/desp/status", Ints.toByteArray(1));
					break;
				case CHILD_UPDATED:
					System.out.println("子元素修改");
//					System.out.println(new String(client.getData().forPath("/desp/status")));
					break;
				case CHILD_REMOVED:
					System.out.println("子元素删除");
					break;
				default:
					break;
				}
			}
		});
		 
//		 client.create().creatingParentsIfNeeded()
//		 	.withMode(CreateMode.EPHEMERAL).forPath("/desp/t1/t2");
		 
		 TimeUnit.SECONDS.sleep(10000);
	}
	
}
