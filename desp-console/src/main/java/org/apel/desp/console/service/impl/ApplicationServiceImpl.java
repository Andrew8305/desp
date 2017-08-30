package org.apel.desp.console.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apel.desp.console.domain.Application;
import org.apel.desp.console.service.ApplicationService;
import org.apel.gaia.infrastructure.impl.AbstractBizCommonService;

@Service
@Transactional
public class ApplicationServiceImpl extends AbstractBizCommonService<Application, String> implements ApplicationService{

	

}
