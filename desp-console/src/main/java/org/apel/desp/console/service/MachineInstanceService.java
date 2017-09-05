package org.apel.desp.console.service;

import org.apel.desp.commons.monitor.AgentMonitorInfo;
import org.apel.desp.console.domain.MachineInstance;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.infrastructure.BizCommonService;

public interface MachineInstanceService extends BizCommonService<MachineInstance,String>{

	
	void pageQueryForUnDeployApp(PageBean pageBean, String appId);
	
	void pageQueryForDeployedApp(PageBean pageBean, String appId);

	void clearAllStatus();

	void updateStatus(String macAddress, String agentStatus, String agentVersion);

	void pageQueryForDeployedStaticApp(PageBean pageBean, String appId);
	
	void pageQueryWithDeploySerial(PageBean pageBean, String appPrimary);

	AgentMonitorInfo getMonitorInfo(String id);
	
}
