package org.apel.desp.commons.monitor;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CpuInfo {

	private double sysTime;
	private double userTime;
	private double idleTime;
	private String sysTimeDisplay;
	private String userTimeDisplay;
	private String idleTimeDisplay;
}
