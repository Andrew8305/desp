package org.apel.desp.commons.consist;

public interface ZKNodePath {

	public final static String ZK_ROOT_PATH = "/desp";//zk根节点
	public final static String ZK_COMMONDS_PATH = "/desp/commonds"; //监听console维护的commonds节点
	public final static String ZK_ACTIVE_AGENTS_PATH = "/desp/activeAgents"; //监听angent维护的活跃节点
	
	public static String getLeafNodeName(String path){
		String[] split = path.split("/");
		return split[split.length - 1];
		
	}
}
