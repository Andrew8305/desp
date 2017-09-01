package org.apel.desp.console.dao;

import java.util.List;

import org.apel.desp.console.domain.MachineInstance;
import org.apel.gaia.persist.dao.CommonRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MachineInstanceRepository extends CommonRepository<MachineInstance, String>{

	List<MachineInstance> findByAgentStatus(String agentStatus);

	@Modifying
	@Query("update MachineInstance set agentVersion = null,agentStatus = null")
	void clearAllStatus();
	
	List<MachineInstance> findByMacAddress(String macAddress);

}
