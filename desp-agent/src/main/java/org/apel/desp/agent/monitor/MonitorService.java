package org.apel.desp.agent.monitor;

import java.io.File;

import org.apel.desp.commons.monitor.AgentMonitorInfo;

public interface MonitorService {

	//本地存储app的文件夹
	public static final File LOCAL_APP_ROOT_DIR = new File(System.getProperty("user.dir") + "/apps");
	
	/**
	 * 更新app的状态
	 */
	public AgentMonitorInfo changeAppStatus(String appId, int status);
	
	/**
	 * 更新app的状态并推送
	 */
	public void changeAppStatusAndUpdate(String appId, int status);
	
	/**
	 * 定时检查agent上的基础信息以及其上运行的app信息，并进行更新
	 */
	public void scheduleSendMonitorInfo();

	/**
	 * 将agent的监控信心推送给zk
	 */
	void updateZkAgentMonitorInfo(AgentMonitorInfo agentMonitorInfo);
	
}
