package org.apel.desp.console.service;

import org.apel.desp.console.domain.DeploySerial;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.infrastructure.BizCommonService;

public interface DeploySerialService extends BizCommonService<DeploySerial,String>{

	void pageByAppPrimary(String appPrimary, String mid, PageBean pageBean);

	
	
	
}
