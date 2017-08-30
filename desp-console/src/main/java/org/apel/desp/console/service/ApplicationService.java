package org.apel.desp.console.service;

import java.io.InputStream;

import org.apel.desp.console.domain.Application;
import org.apel.gaia.infrastructure.BizCommonService;

public interface ApplicationService extends BizCommonService<Application,String>{

	void uploadJar(InputStream is, String fileName, String id);
	
}
