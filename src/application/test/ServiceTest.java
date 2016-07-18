package application.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import application.model.User;
import application.util.InfUtils;
import application.util.base.HttpUtils;

public class ServiceTest {

	
	//@Test
	public void jsonNode() {
		String jsonString = "{\"resultCode \": 0,\" resultMsg \": \"验证成功\",\"result\": {\"token\": \"8e6d0919c99d03238e6d0919c99d0323\",\"url\": \"http://ip:port/a/index?token=\"}}";
		JSONObject js = JSON.parseObject(jsonString, JSONObject.class);
		String rs = js.getJSONObject("result").getString("token");
		System.out.println(rs);
	}
	@Test
	public void infPost() {
		String url = "https://api.weixin.qq.com/hardware/mydevice/platform/get_device_status?access_token=xxx";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "wang");
		JSON js = InfUtils.synPost(url, map);
		System.out.println(js);
	}

	// @Test
	public void get() {
		String url = "https://api.weixin.qq.com/hardware/mydevice/platform/get_device_status?access_token=xxx";
		HttpUtils.synGet(url);
	}

	// @Test
	public void map2Json() {
		List<String> list = new ArrayList<String>();
		list.add("Englist");
		list.add("Chinese");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "wang");
		map.put("age", 10);
		map.put("list", list);
		map.put("user", new User("abc","1123"));

		String rs = JSON.toJSONString(map);
		System.out.println("rs=" + rs);
	}

	 //@Test
	public void string2Json() {
		String jsonString = "{\"name\":\"wang\",\"list\":[\"Englist\",\"Chinese\"],\"age\":10,\"obj\":{\"name\":\"wang\",\"list\":[\"Englist\",\"Chinese\"],\"age\":10}}";
		
		
		
		Map<String, Object> rs = JSON.parseObject(jsonString);
		for (Map.Entry<String, Object> entry : rs.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}

	}

}
