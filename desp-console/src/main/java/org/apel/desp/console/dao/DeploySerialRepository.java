package org.apel.desp.console.dao;

import java.util.List;

import org.apel.desp.console.domain.DeploySerial;
import org.apel.gaia.persist.dao.CommonRepository;

public interface DeploySerialRepository extends CommonRepository<DeploySerial, String>{

	List<String> findByJarRealName(String jarRealName);

	List<DeploySerial> findByApplicationIdAndMachineInstanceIdAndJarRealName(String applicationId, String mId, String jarRealName);
			
	List<DeploySerial> findByApplicationId(String applicationId);
}
