package org.apel.desp.agent.command;

import org.apel.desp.commons.consist.ZKCommandCode;
import org.apel.desp.commons.domain.ZKCommand;

public interface ZKCommander {

	ZKCommandCode commandCode();
	
	void execute(ZKCommand zkCommand, ZKCommandCallback zkCommandCallback);
	
}
