package application;

public class Constants {

	public static final String SERVER_IP = "";
	public static final int SERVER_PORT = 0;
	
	public static final String URL_LOGIN = "URL_LOGIN"; // 登录验证接口
	public static final String URL_LOGOUT = "URL_LOGOUT"; // 退出客户端接口
	public static final String URL_GETMESSAGE = "URL_GETMESSAGE"; //消息接口
	
	
	// error Code
	public static final boolean isDebug = true;
	public static final int RES_CODE_PARAM_CHECK_ERROR = 901;
	public static final String RES_MESSAGE_PARAM_CHECK_ERROR = "RES_MESSAGE_PARAM_CHECK_ERROR";
	
	
	public static final int RES_CODE_DEFAULT = 999;
	public static final String RES_MESSAGE_DEFAULT = "the error from Server";
	
}
