package org.apel.desp.agent.util;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Who;

/**
 * 配合HP sigar获取内存和cpu信息
 * @author lijian
 *
 */
public class SigarUtil {

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
	
	private static void who() throws SigarException {
        Sigar sigar = new Sigar();
        Who who[] = sigar.getWhoList();
        if (who != null && who.length > 0) {
            for (int i = 0; i < who.length; i++) {
                // System.out.println("当前系统进程表中的用户名" + String.valueOf(i));
                Who _who = who[i];
                System.out.println("用户控制台:  " + _who.getDevice());
                System.out.println("用户host: " + _who.getHost());
                // System.out.println("getTime():   " + _who.getTime());
                // 当前系统进程表中的用户名
                System.out.println("当前系统进程表中的用户名:   " + _who.getUser());
            }
        }
    }
	
	public static void main(String[] args) throws Exception {
		who();
	}
}
