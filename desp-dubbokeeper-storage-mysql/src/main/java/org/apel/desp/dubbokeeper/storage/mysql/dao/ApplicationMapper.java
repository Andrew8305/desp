package org.apel.desp.dubbokeeper.storage.mysql.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apel.desp.dubbokeeper.storage.domain.ApplicationInfo;

/**
 * @date: 2015/12/17.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mysql.mapper.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
public interface ApplicationMapper {

    public int addApplication(ApplicationInfo applicationInfo);

    public List<ApplicationInfo> listApps();


    public int getAppType(@Param("name")String name);

    public int updateAppType(@Param("name")String name,@Param("type")int type);

}
