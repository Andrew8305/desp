package org.apel.desp.commons.domain;

import lombok.Getter;
import lombok.Setter;

import org.apel.desp.commons.consist.ZKCommandCode;

@Setter
@Getter
public class ZKCommand {

	//发布的应用标识
	private String appId;
	
	//发出的命令码
	private ZKCommandCode zkCommandCode;
	
	//执行状态：0代表未执行,1代表执行
	private int status;
}
