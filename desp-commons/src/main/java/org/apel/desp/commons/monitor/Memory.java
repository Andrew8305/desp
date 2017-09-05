package org.apel.desp.commons.monitor;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Memory {

	private long total;
	private long used;
	private long free;
	private double usedPercent;
	private String totalUnitG;
	private String usedUnitG;
	private String freeUnitG;
	
}
