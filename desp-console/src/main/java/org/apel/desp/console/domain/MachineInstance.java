package org.apel.desp.console.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 物理机实例
 * @author lijian
 *
 */
@Entity
@Table(name = "desp_machine")
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
	// agent状态
	private String agentStatus;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getMachineInstanceName() {
		return machineInstanceName;
	}

	public void setMachineInstanceName(String machineInstanceName) {
		this.machineInstanceName = machineInstanceName;
	}

	public String getCpuAndMemory() {
		return cpuAndMemory;
	}

	public void setCpuAndMemory(String cpuAndMemory) {
		this.cpuAndMemory = cpuAndMemory;
	}

	public String getAgentVersion() {
		return agentVersion;
	}

	public void setAgentVersion(String agentVersion) {
		this.agentVersion = agentVersion;
	}

	public String getOutterIP() {
		return outterIP;
	}

	public void setOutterIP(String outterIP) {
		this.outterIP = outterIP;
	}

	public String getInnerIP() {
		return innerIP;
	}

	public void setInnerIP(String innerIP) {
		this.innerIP = innerIP;
	}

	public String getAgentStatus() {
		return agentStatus;
	}

	public void setAgentStatus(String agentStatus) {
		this.agentStatus = agentStatus;
	}

}
