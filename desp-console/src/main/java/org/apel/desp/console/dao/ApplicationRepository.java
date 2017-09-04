package org.apel.desp.console.dao;

import java.util.List;

import org.apel.desp.console.domain.Application;
import org.apel.gaia.persist.dao.CommonRepository;

public interface ApplicationRepository extends CommonRepository<Application, String>{

	List<Application> findByAppId(String appId);

}
