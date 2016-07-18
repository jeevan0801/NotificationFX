package application;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import application.model.User;
import application.util.InfUtils;

/** 业务逻辑
 * @author wangjc 
 */
public class Service {

	public JSONObject login(User user) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("userName", user.getUserName());
		userMap.put("passWord", user.getPassWord());

		return InfUtils.synPost(Constants.URL_LOGIN, userMap);
	}

	public JSONObject getMessage(Map<String, Object> messageParam) {
		return InfUtils.synPost(Constants.URL_GETMESSAGE, messageParam);
	}

	public JSONObject logout(String token, User user) {
		Map<String, Object> logoutMap = new HashMap<String, Object>();
		logoutMap.put("token", token);
		logoutMap.put("userName", user.getUserName());
		logoutMap.put("passWord", user.getPassWord());

		return InfUtils.synPost(Constants.URL_LOGOUT, logoutMap);
	}
}
