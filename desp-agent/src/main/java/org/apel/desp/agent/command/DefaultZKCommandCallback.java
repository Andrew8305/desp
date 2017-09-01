package org.apel.desp.agent.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.domain.ZKCommand;

import com.alibaba.fastjson.JSON;

@RequiredArgsConstructor
public class DefaultZKCommandCallback implements ZKCommandCallback{

	@NonNull private CuratorFramework client;
	@NonNull private ZKCommand zkCommand;
	@NonNull private PathChildrenCacheEvent event;
	
	@Override
	public void call() {
		zkCommand.setStatus(SystemConsist.COMMAND_EXE_STATUS_DONE);
		try {
			client.setData().forPath(event.getData().getPath(), JSON.toJSONString(zkCommand).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
