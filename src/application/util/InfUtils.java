package application.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import application.Constants;
import application.model.ServerResponse;
import application.util.base.HttpUtils;

/**
 * @author wangjc
 * 接口调用
 */
public class InfUtils {

	public static JSONObject synPost(String url, Map<String,Object> formParam) {
		JSONObject res = new JSONObject();
		if(Constants.isDebug) {
			String jsonString = "";
			//test
			if (url.equals(Constants.URL_LOGIN)) {
				jsonString = "{\"resultCode \": 0,\" resultMsg \": \"验证成功\",\"result\": {\"token\": \"8e6d0919c99d03238e6d0919c99d0323\",\"url\": \"http://ip:port/a/index?token=\"}}";
			}else if (url.equals(Constants.URL_GETMESSAGE)) {
				jsonString = "{\"resultCode\":0,\"resultMsg\":\"成功\",\"result\":[{\"title\":\"某某企业报告审批\",\"time\":\"2016-05-20 10:12:22\",\"url\":\"http://localhost:9090/web/waitRead/gotoTodoPage?msgId=8\"},{\"title\":\"某某企业预警通知\",\"time\":\"2016-05-20 10:12:22\",\"url\":\"http://localhost:9090/web/warn/gotoWarnPage?wId=20160607901001\"}]}";
			}else if (url.equals(Constants.URL_LOGOUT)) {
				jsonString = "{\"resultCode\":0,\"resultMsg\":\"成功\"}";
			}
			res = JSON.parseObject(jsonString);
			return res;
		}
		
		ServerResponse response = HttpUtils.synPost(url, formParam);
		if (response.isSuccess()) {
			String jsonString = (String)response.getContent();
			res = JSON.parseObject(jsonString);
		}
		return res;
	}
}
