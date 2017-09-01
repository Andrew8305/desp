package org.apel.desp.console.dao;

import java.util.List;

import org.apel.desp.console.domain.CommandEntity;
import org.apel.gaia.persist.dao.CommonRepository;

public interface CommandRepository extends CommonRepository<CommandEntity, String>{

	List<CommandEntity> findByStatusOrderByCreateDateAsc(int status);
	
}
