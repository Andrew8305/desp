package org.apel.desp.agent.command;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.consist.ZKNodePath;
import org.apel.desp.commons.domain.ZKCommand;
import org.apel.desp.commons.monitor.AgentMonitorInfo;
import org.apel.desp.commons.monitor.ApplicationMonitorInfo;
import org.apel.desp.commons.util.NetUtil;

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
			
			AgentMonitorInfo agentMonitorInfo = new AgentMonitorInfo();
			List<ApplicationMonitorInfo> apps = new ArrayList<>();
			ApplicationMonitorInfo applicationMonitorInfo = new ApplicationMonitorInfo();
			applicationMonitorInfo.setAppId("app2");
			applicationMonitorInfo.setStatus(SystemConsist.APPINSTANCE_STATUS_STOPED);
			apps.add(applicationMonitorInfo);
			agentMonitorInfo.setApps(apps);
			String monitorInfo = JSON.toJSONString(agentMonitorInfo);
			String agentPath = ZKNodePath.ZK_ACTIVE_AGENTS_PATH + "/" + NetUtil.getLocalPureMac();
			client.setData().forPath(agentPath, monitorInfo.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
