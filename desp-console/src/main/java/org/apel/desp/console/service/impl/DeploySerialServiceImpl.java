package org.apel.desp.console.service.impl;

import java.util.ArrayList;

import org.apel.desp.console.domain.DeploySerial;
import org.apel.desp.console.service.DeploySerialService;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.infrastructure.impl.AbstractBizCommonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@Service
@Transactional
public class DeploySerialServiceImpl extends AbstractBizCommonService<DeploySerial, String> implements DeploySerialService{

	@Override
	public void pageByAppPrimary(String appPrimary, String mid, PageBean pageBean) {
		String hql = "select d.id,d.jarRealName,d.jarName,d.deployDate from DeploySerial d left join d.application a left join d.machineInstance m "
				+ "where 1=1 and a.id = ? and m.id = ?";
		ArrayList<Object> params = Lists.newArrayList();
		params.add(appPrimary);
		params.add(mid);
		getRepository().doPager(pageBean, hql, params);
	}

	

}
