package org.apel.desp.console.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 应用实例
 * 
 * @author lijian
 *
 */
@Entity
@Table(name = "desp_application")
@Setter
@Getter
public class Application {

	@Id
	private String id;

	private Date createDate;

	// 应用说明
	private String applicationIntro;
	// 应用标识
	private String appId;
	// 负责人
	private String charger;
	// 全部实例数
	private Integer allInstanceNum;
	// 运行实例数
	private Integer runningInstanceNum;
	// 应用名称
	private String applicationName;
	//程序包显示名
	private String jarName;
	//程序包真实名称
	private String jarRealName;
	//jar远程路径
	private String remoteJarPath;

}
