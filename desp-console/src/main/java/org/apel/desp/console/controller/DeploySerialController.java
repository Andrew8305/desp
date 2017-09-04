package org.apel.desp.console.controller;

import org.apel.desp.console.service.DeploySerialService;
import org.apel.gaia.commons.jqgrid.QueryParams;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.util.jqgrid.JqGridUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/deploySerial")
public class DeploySerialController {

	@Autowired
	private DeploySerialService deploySerialService;
	
	//查询发布流水
	@RequestMapping("/pageList/{appPrimary}")
	public @ResponseBody PageBean pageQueryForStartableApp(QueryParams queryParams, 
			@PathVariable("appPrimary")String appPrimary, String mid){
		PageBean pageBean = JqGridUtil.getPageBean(queryParams);
		deploySerialService.pageByAppPrimary(appPrimary, mid, pageBean);
		return pageBean;
	}
	
}
