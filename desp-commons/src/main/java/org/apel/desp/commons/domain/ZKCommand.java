package org.apel.desp.commons.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ZKCommand {

	//主键
	private String id;
	
	//发布的应用标识
	private String appId;
	
	//发出的命令码
	private int zkCommandCode;
	
	//参数
	private String param;
	
	//执行状态：0代表未执行,1代表执行
	private int status;
}
