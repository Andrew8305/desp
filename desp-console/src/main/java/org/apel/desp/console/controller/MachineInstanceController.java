package org.apel.desp.console.controller;

import java.util.List;

import org.apel.desp.console.domain.MachineInstance;
import org.apel.desp.console.service.MachineInstanceService;
import org.apel.gaia.commons.i18n.Message;
import org.apel.gaia.commons.i18n.MessageUtil;
import org.apel.gaia.commons.jqgrid.QueryParams;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.util.jqgrid.JqGridUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/machineInstance")
public class MachineInstanceController {
	
	private final static String INDEX_URL = "machineInstance_index";
	
	@Autowired
	private MachineInstanceService machineInstanceService;
	
	//首页
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(){
		return INDEX_URL;
	}
	
	//列表查询
	@RequestMapping
	public @ResponseBody PageBean list(QueryParams queryParams){
		PageBean pageBean = JqGridUtil.getPageBean(queryParams);
		machineInstanceService.pageQuery(pageBean);
		return pageBean;
	}
	
	//查询没有指定app发布的机器实例
	@RequestMapping("/list/forUnDeployApp")
	public @ResponseBody PageBean pageQueryForUnDeployApp(QueryParams queryParams, String appId){
		PageBean pageBean = JqGridUtil.getPageBean(queryParams);
		machineInstanceService.pageQueryForUnDeployApp(pageBean, appId);
		return pageBean;
	}

	//查询有指定app发布的机器实例
	@RequestMapping("/list/forDeployedApp")
	public @ResponseBody PageBean pageQueryForDeployedApp(QueryParams queryParams, String appId){
		PageBean pageBean = JqGridUtil.getPageBean(queryParams);
		machineInstanceService.pageQueryForDeployedApp(pageBean, appId);
		return pageBean;
	}
	
	//新增
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Message create(MachineInstance machineInstance){
		machineInstanceService.save(machineInstance);
		return MessageUtil.message("machineInstance.create.success");
	}
	
	//更新
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public @ResponseBody Message create(@PathVariable String id, MachineInstance machineInstance){
		machineInstance.setId(id);
		machineInstanceService.update(machineInstance);
		return MessageUtil.message("machineInstance.update.success");
	}
	
	//查看
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody MachineInstance view(@PathVariable String id){
		return machineInstanceService.findById(id);
	}
	
	//删除
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Message delete(@PathVariable String id){
		machineInstanceService.deleteById(id);
		return MessageUtil.message("machineInstance.delete.success");
	}
	
	//批量删除
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody Message batchDelete(@RequestParam("ids[]") String[] ids){
		machineInstanceService.deleteById(ids);
		return MessageUtil.message("machineInstance.delete.success");
	}
	
	//查询全部数据
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody List<MachineInstance> getAll(){
		return machineInstanceService.findAll(new Sort(Direction.DESC, "createDate"));
	}
	
	
	
}
