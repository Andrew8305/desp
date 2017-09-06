package org.apel.desp.console.dao;

import java.util.List;

import org.apel.desp.console.domain.AppInstance;
import org.apel.gaia.persist.dao.CommonRepository;

public interface AppInstanceRepository extends CommonRepository<AppInstance, String>{

	List<AppInstance> findByApplicationIdAndMachineInstanceId(String appPrimary, String mId);
	
	List<AppInstance> findByApplicationAppIdAndMachineInstanceId(String appId, String mId);

	List<AppInstance> findByApplicationIdAndMachineInstanceAgentStatusAndStatus(
			String appPrimary, String agentStatusRunning,
			int appinstanceStatusStoped);
	
	int countByApplicationIdAndStatus(String appPrimary, int status);
	
}
