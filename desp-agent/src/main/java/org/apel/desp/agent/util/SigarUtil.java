package org.apel.desp.agent.util;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * 配合HP sigar获取内存和cpu信息
 * @author lijian
 *
 */
public class SigarUtil {

	 
	public static void main(String[] args) {
		System.out.println(getCpuNums());
	}
	
	/**
	 * 获取计算机内存
	 */
	public static String getTotalMemory(){
		Sigar sigar = new Sigar();
		Mem mem = null;
		String totalMemory = "";
		try {
			mem = sigar.getMem();
			totalMemory = String.valueOf(mem.getTotal() / 1000L / 1000L / 1000L) + "G";
		} catch (SigarException e) {
			e.printStackTrace();
			totalMemory = "检测出错";
		}
		return totalMemory;	
	}
	
	
	/**
	 * 获取处理器个数(等于一般等于线程数)
	 * @return
	 */
	public static int getCpuNums(){
		Sigar sigar = new Sigar();
		int cpuNums = 0;
		try {
			cpuNums = sigar.getCpuPercList().length;
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return cpuNums;
	}
	
}
