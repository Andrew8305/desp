package org.apel.desp.console.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 物理机实例
 * @author lijian
 *
 */
@Entity
@Table(name = "desp_machine")
@Setter
@Getter
public class MachineInstance {

	@Id
	private String id;

	private Date createDate;

	// 实例名称
	private String machineInstanceName;
	// CPU/内存
	private String cpuAndMemory;
	// agent版本
	private String agentVersion;
	// 内网IP
	private String outterIP;
	// 外网IP
	private String innerIP;
	// mac地址
	private String macAddress;
	// agent状态
	private String agentStatus;

}
