package org.apel.desp.console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.apel.gaia.commons.consist.CommonConsist;
import org.apel.gaia.commons.i18n.Message;
import org.apel.gaia.commons.i18n.MessageUtil;
import org.apel.gaia.util.encryption.MD5Util;
import org.apel.poseidon.api.CasService;
import org.apel.poseidon.api.SystemUserService;
import org.apel.poseidon.api.domain.SystemMenu;
import org.apel.poseidon.api.domain.SystemUser;
import org.apel.poseidon.security.commons.util.UserDetailsUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;

/**
 * 控制台首页 controller
 * @author lijian
 *
 */
@Controller
@RequestMapping("/")
public class UIIndexController {
	
	private static String CONSOLE = "console";
	private static String INDEX = "index";
	
	@Reference
	private SystemUserService systemUserProvider;
	@Reference
	private CasService casProvider;
	@Value("${cas_server_url}")
	private String casIndexUrl;
	
	@RequestMapping
	public String index() throws Exception{
		return INDEX;
	}
	
	@RequestMapping("console")
	public String console() throws Exception{
		return CONSOLE;
	}

	//加载控制台首页中的系统菜单
	@RequestMapping(value="loadMenu")
	public @ResponseBody List<SystemMenu> loadFunctionMenu() throws Exception{
		return UserDetailsUtil.getCurrentUser().getFunctionMenus();
	}
	
	//验证是否旧密码是否正确
	@RequestMapping("checkPassword")
	public @ResponseBody String checkPassword(String oldPassword){
		if(!StringUtils.isEmpty(oldPassword)){
			oldPassword = MD5Util.encodeMD5Hex(oldPassword);
		}
		SystemUser user = systemUserProvider.findSystemUserByUserId(UserDetailsUtil.getCurrentUser().getId());
		String result = "";
		if(Objects.isNull(user)){
			result = "{\"valid\":false}";
		}else{
			if(user.getUserPassword().equals(oldPassword)){
				result = "{\"valid\":true}";
			}else{
				result = "{\"valid\":false}";
			}
		}
		return result;
	}
	
	//修改密码
	@RequestMapping(value = "updatePassword", method = RequestMethod.POST)
	public @ResponseBody Message updatePassword(String newPassword){
		systemUserProvider.updatePassword(UserDetailsUtil.getCurrentUserId(), newPassword);
		return MessageUtil.message("update_password_success"); 
	}
	
	
	//获取当前用户的基本信息，展示在控制台首页的modal框中
	@RequestMapping(value="currentUser/baseInfo")
	public @ResponseBody Map<String, Object> currentUser(HttpSession session) throws Exception{
		SystemUser systemUser = systemUserProvider.findSystemUserByUserId(UserDetailsUtil.getCurrentUser().getId());
		String alias = systemUser.getAlias();
		String email = systemUser.getEmail();
		String personLogo = session.getAttribute(CommonConsist.CURRENT_USER_LOGO) == null ? "" : session.getAttribute(CommonConsist.CURRENT_USER_LOGO).toString();
		String userName = UserDetailsUtil.getCurrentUser().getUsername();
		String personName = "";
		if(UserDetailsUtil.getCurrentUser().getPerson() != null){
			personName = UserDetailsUtil.getCurrentUser().getPerson().getPersonName();
		}
		Map<String, Object> result = new HashMap<>();
		result.put("userName", userName);
		result.put("personName", personName);
		result.put("personLogo", personLogo);
		result.put("alias", alias);
		result.put("email", email);
		return result;
	} 
	
	//获取cas index url
	@RequestMapping(value="getCasIndexUrl")
	public @ResponseBody String getCasIndexUrl() throws Exception{
		return casIndexUrl;
	}
	
}
