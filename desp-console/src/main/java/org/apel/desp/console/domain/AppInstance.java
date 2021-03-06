package org.apel.desp.console.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "desp_app_instance")
@Setter
@Getter
public class AppInstance {

	@Id
	private String id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "app_id")
	private Application application;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "m_id")
	private MachineInstance machineInstance;
	
	//部署的jar包名称
	private String jarName;
	
	//运行状态 
	private int status;
	
	private Date createDate;
	
}
