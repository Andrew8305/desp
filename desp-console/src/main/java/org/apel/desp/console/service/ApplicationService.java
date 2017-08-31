package org.apel.desp.console.service;

import java.io.InputStream;

import org.apel.desp.console.domain.Application;
import org.apel.gaia.infrastructure.BizCommonService;

public interface ApplicationService extends BizCommonService<Application,String>{

	/**
	 * 上传jar包
	 * @param is 输入流
	 * @param fileName 文件名称
	 * @param id 应用主键
	 */
	void uploadJar(InputStream is, String fileName, String id);

	void deploy(String[] mids, String appPrimary);
	
	void deployAll(String appPrimary);
	
}
