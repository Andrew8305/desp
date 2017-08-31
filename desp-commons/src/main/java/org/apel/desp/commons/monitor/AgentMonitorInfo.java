package org.apel.desp.commons.monitor;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AgentMonitorInfo {

	private List<ApplicationMonitorInfo> apps;
	
}
