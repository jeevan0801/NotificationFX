package application.util.base;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import application.Constants;
import application.model.ServerResponse;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
	private static OkHttpClient client = new OkHttpClient();
	private static final int TIMEOUT_SECOND = 3;
	static {
		client.newBuilder().connectTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS)
				.writeTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS).readTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS);
	}

	// 同步Post提交
	public static ServerResponse synPost(String url, Map<String, Object> formParam) {
		ServerResponse rs = new ServerResponse();
		FormBody.Builder builder = new FormBody.Builder();

		for (Entry<String, Object> entry : formParam.entrySet()) {
			builder.add(entry.getKey(), (String) entry.getValue());
		}

		RequestBody body = builder.build();
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = null;
		try {
			response = client.newCall(request).execute();

			if (response.isSuccessful()) {
				rs.setSuccess(true);
				rs.setCode(response.code());
				rs.setContent(response.body().string());
			}else{
				rs.setSuccess(false);
				rs.setCode(Constants.RES_CODE_DEFAULT);
				rs.setMessage(Constants.RES_MESSAGE_DEFAULT);
			}
			
			response.close();
		} catch (IOException e) {
			rs.setSuccess(false);
			rs.setCode(Constants.RES_CODE_DEFAULT);
			rs.setMessage(e.getMessage());
			e.printStackTrace();
		}
		
		return rs;
	}
	
	// 同步 Get 请求
	public static ServerResponse synGet(String url) {
		ServerResponse rs = new ServerResponse();
		Request request = new Request.Builder().url(url).build();
		Response response = null;
		try {
			response = client.newCall(request).execute();

			if (response.isSuccessful()) {
				rs.setSuccess(true);
				rs.setCode(response.code());
				rs.setContent(response.body().string());
			}else{
				rs.setSuccess(false);
				rs.setCode(Constants.RES_CODE_DEFAULT);
				rs.setMessage(Constants.RES_MESSAGE_DEFAULT);
			}
			
			response.close();
		} catch (IOException e) {
			rs.setSuccess(false);
			rs.setCode(Constants.RES_CODE_DEFAULT);
			rs.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return rs;
	}
	
	
	// 打开url 在系统默认浏览器中
	public static void openUrlByBrowser(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception e1) {
			System.err.println("openUrlByBrowser error: "+ url);
			e1.printStackTrace();
		}
	}
	
}
