package org.apel.desp.console.dao;

import java.util.List;

import org.apel.desp.console.domain.DeploySerial;
import org.apel.gaia.persist.dao.CommonRepository;

public interface DeploySerialRepository extends CommonRepository<DeploySerial, String>{

	List<String> findByJarRealName(String jarRealName);

	List<DeploySerial> findByApplicationIdAndMiIdAndJarRealName(String applicationId, String mId, String jarRealName);
			
	
}
