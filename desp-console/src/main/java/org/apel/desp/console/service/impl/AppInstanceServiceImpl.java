package org.apel.desp.console.service.impl;

import java.util.List;

import org.apel.desp.commons.monitor.ApplicationMonitorInfo;
import org.apel.desp.console.dao.AppInstanceRepository;
import org.apel.desp.console.dao.MachineInstanceRepository;
import org.apel.desp.console.domain.AppInstance;
import org.apel.desp.console.domain.MachineInstance;
import org.apel.desp.console.service.AppInstanceService;
import org.apel.gaia.infrastructure.impl.AbstractBizCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppInstanceServiceImpl extends AbstractBizCommonService<AppInstance, String> implements AppInstanceService{

	@Autowired
	private MachineInstanceRepository machineInstanceRepository;
	
	@Override
	public void updateStatus(String macAddress, List<ApplicationMonitorInfo> apps) {
		if (apps != null){
			List<MachineInstance> mis = machineInstanceRepository.findByMacAddress(macAddress);
			if (mis.size() != 0 && null != apps){
				for (ApplicationMonitorInfo appMonitorInfo : apps) {
					List<AppInstance> appInstances = ((AppInstanceRepository)getRepository()).
						findByApplicationAppIdAndMachineInstanceId(
								appMonitorInfo.getAppId(), 
							mis.get(0).getId()
						);
					for (AppInstance appInstance : appInstances) {
						appInstance.setStatus(appMonitorInfo.getStatus());
						getRepository().update(appInstance);
					}
				}
			}
		}
	}

	
	

}
