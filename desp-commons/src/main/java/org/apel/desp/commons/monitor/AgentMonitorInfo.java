package org.apel.desp.commons.monitor;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AgentMonitorInfo {

	private String agentVersion;
	private String status;
	private List<ApplicationMonitorInfo> apps;
	
}
