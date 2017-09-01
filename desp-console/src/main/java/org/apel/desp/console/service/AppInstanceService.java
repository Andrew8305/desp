package org.apel.desp.console.service;

import java.util.List;

import org.apel.desp.commons.monitor.ApplicationMonitorInfo;
import org.apel.desp.console.domain.AppInstance;
import org.apel.gaia.infrastructure.BizCommonService;

public interface AppInstanceService extends BizCommonService<AppInstance,String>{

	void updateStatus(String macAddress, List<ApplicationMonitorInfo> apps);

	
}
