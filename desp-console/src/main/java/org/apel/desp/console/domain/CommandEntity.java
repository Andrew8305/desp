package org.apel.desp.console.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "desp_command")
@Setter
@Getter
public class CommandEntity {

	@Id
	private String id;
	
	private String appId;
	
	//agent的标识，逗号相隔
	private String agents;
	
	//zkCommandCode整形表达
	private int zkCommandCode;
	
	//执行状态 0代表未执行 1代表已执行
	private int status;
	
	
	
}
