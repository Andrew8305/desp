package org.apel.desp.commons.consist;

/**
 * console对agent发出的命令枚举
 * @author lijian
 *
 */
public enum ZKCommandCode {

	PULL_FILE(1),//FTP拉取文件
	START_APP(2),//启动应用
	STOP_APP(3), //停止应用 
	DEL_APP(4); //删除应用
	
	
	private int code;
	
	private ZKCommandCode(int code){
		this.code = code;
	}
	
	public String toString(){
		return String.valueOf(this.code);
	}
	
	public static ZKCommandCode getZKCommand(int code){
		ZKCommandCode[] values = ZKCommandCode.values();
		for (ZKCommandCode zkCommandCode : values) {
			if (zkCommandCode.code == code){
				return zkCommandCode;
			}
		}
		throw new RuntimeException("没有找到对应的类型的ZKCommandCode");
	}
	
	
}
