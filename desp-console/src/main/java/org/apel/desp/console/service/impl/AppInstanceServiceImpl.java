package org.apel.desp.console.service.impl;

import java.util.List;

import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.monitor.ApplicationMonitorInfo;
import org.apel.desp.console.dao.AppInstanceRepository;
import org.apel.desp.console.dao.MachineInstanceRepository;
import org.apel.desp.console.domain.AppInstance;
import org.apel.desp.console.domain.MachineInstance;
import org.apel.desp.console.service.AppInstanceService;
import org.apel.gaia.commons.pager.Condition;
import org.apel.gaia.commons.pager.Operation;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.commons.pager.RelateType;
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
	
	@Override
	public void pageByAppIdWithAgentActiveAndAppStatusStopped(PageBean pageBean, String appId) {
		Condition c = new Condition();
		c.setPropertyName("m.agentStatus");
		c.setPropertyValue(SystemConsist.AGENT_STATUS_RUNNING);
		c.setOperation(Operation.EQ);
		c.setRelateType(RelateType.AND);
		Condition c2 = new Condition();
		c2.setPropertyName("a.appId");
		c2.setPropertyValue(appId);
		c2.setOperation(Operation.EQ);
		c2.setRelateType(RelateType.AND);
		Condition c3 = new Condition();
		c3.setPropertyName("ai.status");
		c3.setPropertyValue(SystemConsist.APPINSTANCE_STATUS_STOPED);
		c3.setOperation(Operation.EQ);
		c3.setRelateType(RelateType.AND);
		pageBean.getConditions().add(c);
		pageBean.getConditions().add(c2);
		pageBean.getConditions().add(c3);
		String hql = "select ai.id,m.macAddress,m.cpuAndMemory,m.innerIP,m.outterIP,m.machineInstanceName,ai.createDate,ai.status, ai.jarName "
				+ "from AppInstance ai left join ai.machineInstance m left join ai.application a where 1=1";
		getRepository().doPager(pageBean, hql);
	}

	@Override
	public void pageByAppIdWithAgentActiveAndAppStatusActive(PageBean pageBean,
			String appId) {
		Condition c = new Condition();
		c.setPropertyName("m.agentStatus");
		c.setPropertyValue(SystemConsist.AGENT_STATUS_RUNNING);
		c.setOperation(Operation.EQ);
		c.setRelateType(RelateType.AND);
		Condition c2 = new Condition();
		c2.setPropertyName("a.appId");
		c2.setPropertyValue(appId);
		c2.setOperation(Operation.EQ);
		c2.setRelateType(RelateType.AND);
		Condition c3 = new Condition();
		c3.setPropertyName("ai.status");
		c3.setPropertyValue(SystemConsist.APPINSTANCE_STATUS_RUNNING);
		c3.setOperation(Operation.EQ);
		c3.setRelateType(RelateType.AND);
		pageBean.getConditions().add(c);
		pageBean.getConditions().add(c2);
		pageBean.getConditions().add(c3);
		String hql = "select ai.id,m.macAddress,m.cpuAndMemory,m.innerIP,m.outterIP,m.machineInstanceName,ai.createDate,ai.status, ai.jarName "
				+ "from AppInstance ai left join ai.machineInstance m left join ai.application a where 1=1";
		getRepository().doPager(pageBean, hql);
	}

}
