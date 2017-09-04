package org.apel.desp.console.controller;

import org.apel.desp.console.service.AppInstanceService;
import org.apel.gaia.commons.jqgrid.QueryParams;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.util.jqgrid.JqGridUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/appInstance")
public class AppInstanceController {

	@Autowired
	private AppInstanceService appInstanceService;
	
	
	//查询指定的appid并且应用处于停止状态的app运行实例
	@RequestMapping("/list/forStartableApp")
	public @ResponseBody PageBean pageQueryForStartableApp(QueryParams queryParams, String appId){
		PageBean pageBean = JqGridUtil.getPageBean(queryParams);
		appInstanceService.pageByAppIdWithAgentActiveAndAppStatusStopped(pageBean, appId);
		return pageBean;
	}
	
	//查询指定的appid并且应用处于运行状态的app运行实例
	@RequestMapping("/list/forStoppableApp")
	public @ResponseBody PageBean pageQueryForStoppableApp(QueryParams queryParams, String appId){
		PageBean pageBean = JqGridUtil.getPageBean(queryParams);
		appInstanceService.pageByAppIdWithAgentActiveAndAppStatusActive(pageBean, appId);
		return pageBean;
	}
}
