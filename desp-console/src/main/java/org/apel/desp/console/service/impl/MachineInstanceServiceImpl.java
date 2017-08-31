package org.apel.desp.console.service.impl;

import org.apel.desp.commons.consist.SystemConsist;
import org.apel.desp.console.domain.MachineInstance;
import org.apel.desp.console.service.MachineInstanceService;
import org.apel.gaia.commons.pager.Condition;
import org.apel.gaia.commons.pager.Operation;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.commons.pager.RelateType;
import org.apel.gaia.infrastructure.impl.AbstractBizCommonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MachineInstanceServiceImpl extends AbstractBizCommonService<MachineInstance, String> implements MachineInstanceService{

	@Override
	public void pageQueryForUnDeployApp(PageBean pageBean, String appId) {
		Condition c = new Condition();
		c.setPropertyName("m.agentStatus");
		c.setPropertyValue(SystemConsist.AGENT_STATUS_RUNNING);
		c.setOperation(Operation.EQ);
		c.setRelateType(RelateType.AND);
		Condition c2 = new Condition();
		c2.setPropertyName("a.appId");
		c2.setPrefixBrackets(true);
		c2.setPropertyValue(appId);
		c2.setOperation(Operation.NE);
		c2.setRelateType(RelateType.AND);
		Condition c3 = new Condition();
		c3.setPropertyName("a.appId");
		c3.setSuffixBrackets(true);
		c3.setOperation(Operation.NU);
		c3.setRelateType(RelateType.OR);
		pageBean.getConditions().add(c);
		pageBean.getConditions().add(c2);
		pageBean.getConditions().add(c3);
		String hql = "select DISTINCT m.id,m.macAddress,m.cpuAndMemory,m.innerIP,m.outterIP,m.machineInstanceName,m.createDate from AppInstance ai right join ai.machineInstance m left join ai.application a where 1=1";
		getRepository().doPager(pageBean, hql);
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
		String hql = "select m.id,m.macAddress,m.cpuAndMemory,m.innerIP,m.outterIP,m.machineInstanceName,m.createDate,ai.status, a.jarName"
				+ " from AppInstance ai right join ai.machineInstance m left join ai.application a where 1=1";
		getRepository().doPager(pageBean, hql);
	}


	

}
