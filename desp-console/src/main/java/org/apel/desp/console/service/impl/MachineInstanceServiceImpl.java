package org.apel.desp.console.service.impl;

import java.util.List;

import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.commons.consist.ZKNodePath;
import org.apel.desp.commons.monitor.AgentMonitorInfo;
import org.apel.desp.commons.util.ZKConnector;
import org.apel.desp.console.dao.MachineInstanceRepository;
import org.apel.desp.console.domain.MachineInstance;
import org.apel.desp.console.service.MachineInstanceService;
import org.apel.gaia.commons.pager.Condition;
import org.apel.gaia.commons.pager.Operation;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.commons.pager.RelateType;
import org.apel.gaia.infrastructure.impl.AbstractBizCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

@Service
@Transactional
public class MachineInstanceServiceImpl extends AbstractBizCommonService<MachineInstance, String> implements MachineInstanceService{

	@Autowired
	private ZKConnector zkConnector;
	
	@Override
	public void pageQueryForUnDeployApp(PageBean pageBean, String appId) {
		Condition c = new Condition();
		c.setPropertyName("m.agentStatus");
		c.setPropertyValue(SystemConsist.AGENT_STATUS_RUNNING);
		c.setOperation(Operation.EQ);
		c.setRelateType(RelateType.AND);
		pageBean.getConditions().add(c);
		String hql = "select m.id,m.macAddress,m.cpuAndMemory,m.innerIP,m.outterIP,m.machineInstanceName,m.createDate "
		+ "from MachineInstance m where 1=1 and m.id not in "
		+ "(select mm.id from AppInstance ai left join ai.machineInstance mm left join ai.application a "
		+ "where a.appId = ?) ";
		List<Object> params = Lists.newArrayList();
		params.add(appId);
		getRepository().doPager(pageBean, hql, params);
	}

	@Override
	public void pageQueryForDeployedApp(PageBean pageBean, String appId) {
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
		pageBean.getConditions().add(c);
		pageBean.getConditions().add(c2);
		String hql = "select m.id,m.macAddress,m.cpuAndMemory,m.innerIP,m.outterIP,m.machineInstanceName,m.createDate,ai.status, ai.jarName"
				+ " from AppInstance ai right join ai.machineInstance m left join ai.application a where 1=1";
		getRepository().doPager(pageBean, hql);
	}

	@Override
	public void clearAllStatus() {
		((MachineInstanceRepository)getRepository()).clearAllStatus();
	}

	@Override
	public void updateStatus(String macAddress, String agentStatus, String agentVersion) {
		List<MachineInstance> mis = ((MachineInstanceRepository)getRepository()).findByMacAddress(macAddress);
		if (mis.size() != 0){
			for (MachineInstance mi : mis) {
				mi.setAgentVersion(agentVersion);
				mi.setAgentStatus(agentStatus);
				getRepository().update(mi);
			}
		}
	}

	@Override
	public void pageQueryForDeployedStaticApp(PageBean pageBean, String appId) {
		Condition c = new Condition();
		c.setPropertyName("a.appId");
		c.setPropertyValue(appId);
		c.setOperation(Operation.EQ);
		c.setRelateType(RelateType.AND);
		pageBean.getConditions().add(c);
		String hql = "select ai.id,m.macAddress,m.cpuAndMemory,m.innerIP,m.outterIP,"
				+ "m.machineInstanceName,ai.createDate,ai.status, ai.jarName,m.agentVersion,m.agentStatus"
				+ " from AppInstance ai right join ai.machineInstance m left join ai.application a where 1=1";
		getRepository().doPager(pageBean, hql);
	}

	@Override
	public void pageQueryWithDeploySerial(PageBean pageBean, String appPrimary) {
		Condition c = new Condition();
		c.setPropertyName("m.agentStatus");
		c.setPropertyValue(SystemConsist.AGENT_STATUS_RUNNING);
		c.setOperation(Operation.EQ);
		c.setRelateType(RelateType.AND);
		pageBean.getConditions().add(c);
		String hql = "select m.id,m.macAddress,m.cpuAndMemory,m.innerIP,m.outterIP,m.machineInstanceName,m.createDate "
		+ "from MachineInstance m where 1=1 and m.id in "
		+ "(select mm.id from DeploySerial d left join d.machineInstance mm left join d.application a "
		+ "where a.id = ?) ";
		List<Object> params = Lists.newArrayList();
		params.add(appPrimary);
		getRepository().doPager(pageBean, hql, params);
	}

	@Override
	public AgentMonitorInfo getMonitorInfo(String id) {
		MachineInstance machineInstance = findById(id);
		String agent = machineInstance.getMacAddress().replaceAll("[- | :]", "");
		AgentMonitorInfo agentMonitorInfo = new AgentMonitorInfo();
		try {
			byte[] dataBytes = zkConnector.getClient().getData().forPath(ZKNodePath.ZK_ACTIVE_AGENTS_PATH + "/" + agent);
			agentMonitorInfo = JSON.parseObject(new String(dataBytes), AgentMonitorInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
		return agentMonitorInfo;
	}

}
