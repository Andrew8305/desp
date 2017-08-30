package org.apel.desp.console.service.impl;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apel.desp.commons.util.FTPUtil;
import org.apel.desp.console.dao.DeploySerialRepository;
import org.apel.desp.console.domain.Application;
import org.apel.desp.console.service.ApplicationService;
import org.apel.gaia.infrastructure.impl.AbstractBizCommonService;
import org.apel.gaia.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Transactional
public class ApplicationServiceImpl extends AbstractBizCommonService<Application, String> implements ApplicationService{

	@Autowired
	private FTPUtil ftpUtil;
	@Autowired
	private DeploySerialRepository deploySerialRepository;
	
	@Override
	public void uploadJar(InputStream is, String fileName, String id) {
		//先检查之前的jar包是否有发布流水引用，如果没有则需要删除jar
		Application application = findById(id);
		List<String> deploySerials = deploySerialRepository.findByJarRealName(application.getJarRealName());
		if (CollectionUtils.isEmpty(deploySerials) && StringUtils.isNotEmpty(application.getRemoteJarPath())){
			ftpUtil.deleteFile(application.getRemoteJarPath());
		}
		//先保存本地数据库，然后将流嫁接给ftp
		application.setJarName(fileName);
		String remoteJarPath = application.getAppId();
		String jarRealName = UUIDUtil.uuid() + ".jar";
		application.setJarRealName(jarRealName);
		application.setRemoteJarPath(remoteJarPath + "/" + jarRealName);
		update(application);
		ftpUtil.storeFile(is, remoteJarPath, jarRealName);
	}

	

}
