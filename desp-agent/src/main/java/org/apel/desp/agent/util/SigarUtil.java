package org.apel.desp.agent.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apel.desp.commons.monitor.CpuInfo;
import org.apel.desp.commons.monitor.Memory;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.google.common.collect.Lists;

/**
 * 配合HP sigar获取内存和cpu信息
 * @author lijian
 *
 */
public class SigarUtil {

	/**
	 * 获取计算机内存
	 */
	public static Memory calculateMemory(){
		Sigar sigar = new Sigar();
		Mem mem = null;
		Memory memory = new Memory();
		try {
			mem = sigar.getMem();
			memory.setTotal(mem.getTotal());
			memory.setUsed(mem.getUsed());
			memory.setFree(mem.getFree());
			memory.setUsedPercent(mem.getUsedPercent());
			DecimalFormat format = new DecimalFormat("0.00");
			memory.setTotalUnitG(format.format(Double.valueOf(String.valueOf(memory.getTotal())) / 1024 / 1024 / 1024) + "G"); 
			memory.setUsedUnitG(format.format(Double.valueOf(String.valueOf(memory.getUsed())) / 1024 / 1024 / 1024)  + "G"); 
			memory.setFreeUnitG((format.format(Double.valueOf(String.valueOf(memory.getFree())) / 1024 / 1024 / 1024)  + "G")); 
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return memory;	
	}
	
	
	/**
	 * 获取处理器个数(等于一般等于线程数)
	 * @return
	 */
	public static List<CpuInfo> calculateCpuInfo(){
		Sigar sigar = new Sigar();
		ArrayList<CpuInfo> list = Lists.newArrayList();
		try {
			for (CpuPerc cpuPerc : sigar.getCpuPercList()) {
				CpuInfo cpuInfo = new CpuInfo();
				DecimalFormat format = new DecimalFormat("0.00");
				cpuInfo.setSysTime(cpuPerc.getSys());
				cpuInfo.setSysTimeDisplay(format.format(cpuInfo.getSysTime() * 100) + "%");
				cpuInfo.setUserTime(cpuPerc.getUser());
				cpuInfo.setUserTimeDisplay(format.format(cpuInfo.getUserTime() * 100) + "%");
				cpuInfo.setIdleTime(cpuPerc.getIdle());
				cpuInfo.setIdleTimeDisplay(format.format(cpuInfo.getIdleTime() * 100) + "%");
				list.add(cpuInfo);
			}
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return list;
	}
}
