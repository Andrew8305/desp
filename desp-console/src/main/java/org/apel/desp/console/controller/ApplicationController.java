package org.apel.desp.console.controller;

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apel.desp.console.domain.Application;
import org.apel.desp.console.service.ApplicationService;
import org.apel.gaia.commons.i18n.Message;
import org.apel.gaia.commons.i18n.MessageUtil;
import org.apel.gaia.commons.jqgrid.QueryParams;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.util.jqgrid.JqGridUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Throwables;

@Controller
@RequestMapping("/application")
public class ApplicationController {

	private final static String INDEX_URL = "application_index";
	private final static String DETAIL_URL = "application_detail";

	@Autowired
	private ApplicationService applicationService;

	// 首页
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index() {
		return INDEX_URL;
	}
	
	// 详情页
	@RequestMapping(value = "detail", method = RequestMethod.GET)
	public String detail(String id, Model model) {
		if (StringUtils.isEmpty(id)){
			return INDEX_URL;
		}
		Application application = applicationService.findById(id);
		model.addAttribute("application", application);
		return DETAIL_URL;
	}

	// 列表查询
	@RequestMapping
	public @ResponseBody PageBean list(QueryParams queryParams) {
		JqGridUtil.getPageBean(queryParams);
		PageBean pageBean = JqGridUtil.getPageBean(queryParams);
		applicationService.pageQuery(pageBean);
		return pageBean;
	}

	// 新增
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Message create(Application application) {
		applicationService.saveApplication(application);
		return MessageUtil.message("application.create.success");
	}

	// 更新
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public @ResponseBody Message create(@PathVariable String id,
			Application application) {
		application.setId(id);
		applicationService.update(application);
		return MessageUtil.message("application.update.success");
	}

	// 查看
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody Application view(@PathVariable String id) {
		return applicationService.findById(id);
	}

	// 删除
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Message delete(@PathVariable String id) {
		applicationService.deleteById(id);
		return MessageUtil.message("application.delete.success");
	}

	// 批量删除
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody Message batchDelete(@RequestParam("ids[]") String[] ids) {
		applicationService.deleteById(ids);
		return MessageUtil.message("application.delete.success");
	}

	// 查询全部数据
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody List<Application> getAll() {
		return applicationService.findAll(new Sort(Direction.DESC, "createDate"));
	}
	
	//上传jar
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String upload(HttpServletRequest request) {
		try {
			String id = request.getParameter("id");//获取app的数据库主键
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (!isMultipart) {
				return "error";
			}
			ServletFileUpload upload = new ServletFileUpload();

			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				InputStream stream = item.openStream();
				if (!item.isFormField()) {
					String fileName = item.getName();
					applicationService.uploadJar(stream, fileName, id);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Throwables.throwIfUnchecked(e);
		}
		return "success";
	}
	
	//部署
	@RequestMapping(value = "/deploy", method = RequestMethod.POST)
	public @ResponseBody Message deploy(@RequestParam("mids[]")String[] mids, String appPrimary){
		applicationService.deploy(mids, appPrimary);
		return MessageUtil.message("deploy.success");
	}
	
	//全部部署
	@RequestMapping(value = "/deployAll", method = RequestMethod.POST)
	public @ResponseBody Message deployAll(String appPrimary){
		applicationService.deployAll(appPrimary);
		return MessageUtil.message("deploy.success");
	}
	
	//启动
	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public @ResponseBody Message start(@RequestParam("appInstanceIds[]")String[] appInstanceIds){
		applicationService.start(appInstanceIds);
		return MessageUtil.message("start.success");
	}
	
	//全部启动
	@RequestMapping(value = "/startAll", method = RequestMethod.POST)
	public @ResponseBody Message startAll(String appPrimary){
		applicationService.startAll(appPrimary);
		return MessageUtil.message("start.success");
	}
	
	//停止
	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	public @ResponseBody Message stop(@RequestParam("appInstanceIds[]")String[] appInstanceIds){
		applicationService.stop(appInstanceIds);
		return MessageUtil.message("stop.success");
	}
	
	//全部停止
	@RequestMapping(value = "/stopAll", method = RequestMethod.POST)
	public @ResponseBody Message stopAll(String appPrimary){
		applicationService.stopAll(appPrimary);
		return MessageUtil.message("stop.success");
	}
	
	//全部删除
	@RequestMapping(value = "/deleteAll", method = RequestMethod.POST)
	public @ResponseBody Message deleteAll(String appPrimary){
		applicationService.deleteAll(appPrimary);
		return MessageUtil.message("application.delete.success");
	}
	
	//删除
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Message delete(@RequestParam("appInstanceIds[]")String[] appInstanceIds){
		applicationService.delete(appInstanceIds);
		return MessageUtil.message("application.delete.success");
	}
	
	//回滚
	@RequestMapping(value = "/rollback", method = RequestMethod.POST)
	public @ResponseBody Message rollback(String deploySerialId){
		applicationService.rollback(deploySerialId);
		return MessageUtil.message("rollback.success");
	}
	

}
