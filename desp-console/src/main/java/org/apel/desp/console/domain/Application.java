package org.apel.desp.console.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * 应用实例
 * @author lijian
 *
 */
@Entity
@Table(name = "desp_application")
public class Application {

	@Id
	private String id;

	private Date createDate;
	
	// 应用说明
	private String  applicationIntro;
	// 负责人
	private String  charger;
	// 全部实例数
	private Integer  allInstanceNum;
	// 运行实例数
	private Integer  runningInstanceNum;
	// 应用名称
	private String  applicationName;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getApplicationIntro() {
		return applicationIntro;
	}
	public void setApplicationIntro(String applicationIntro) {
		this.applicationIntro = applicationIntro;
	}
	public String getCharger() {
		return charger;
	}
	public void setCharger(String charger) {
		this.charger = charger;
	}
	public Integer getAllInstanceNum() {
		return allInstanceNum;
	}
	public void setAllInstanceNum(Integer allInstanceNum) {
		this.allInstanceNum = allInstanceNum;
	}
	public Integer getRunningInstanceNum() {
		return runningInstanceNum;
	}
	public void setRunningInstanceNum(Integer runningInstanceNum) {
		this.runningInstanceNum = runningInstanceNum;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

}
