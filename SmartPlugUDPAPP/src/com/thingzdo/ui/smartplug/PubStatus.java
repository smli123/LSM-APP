package com.thingzdo.ui.smartplug;

public class PubStatus {
	public enum LoginState{
		LOGIN_ONLINE,
		LOGIN_OFFLINE
	};
	
	private static LoginState mLoginState;
	
	public static String g_CurUserName;
	public static String g_userEmail;
	public static String g_userPwd;
	public static String g_userCookie;
	public static String g_moduleId;
	public static String g_moduleType;
	
	public static void setLoginState(LoginState state) {
		mLoginState = state;	
	}
	
	public static LoginState getLoginState() {
	    return mLoginState;	
	}
	
	public static String getUserName() {
		if (null == g_CurUserName || g_CurUserName.isEmpty()) {
			return "Thingzdo";
		}

		return g_CurUserName;
	}
}
