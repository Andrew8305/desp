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

/**
 * 发布流水
 * @author lijian
 *
 */
@Entity
@Table(name = "desp_deploy_serial")
@Setter
@Getter
public class DeploySerial {

	@Id
	private String id;
	
	private Date deployDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "m_id")
	private MachineInstance mi;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "app_id")
	private Application application;
	
	private String jarRealName;
	
	private String jarName;
}
