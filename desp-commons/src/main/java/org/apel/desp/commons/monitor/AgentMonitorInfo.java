package org.apel.desp.commons.monitor;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AgentMonitorInfo {

	private List<CpuInfo> cpus;
	private Memory memory;
	private String agentVersion;
	private String status;
	private List<ApplicationMonitorInfo> apps;
	
}
