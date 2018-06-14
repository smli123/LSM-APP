package com.thingzdo.ui.smartplug;

import java.util.ArrayList;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.CmdDefine;
import com.thingzdo.ui.common.ServerErrCodeMgr;

public class AppServerReposeDefine {
	public static final int Cmd_OK      = 1;
	public static final int Cmd_Fail    = 0;
	public static final int Cmd_Unknown = -1;
	
	public static final int Socket_Connect_OK    = 1;
	public static final int Socket_Connect_FAIL  = 0;
	public static final int Socket_Connect_EXCEPTION = -1;
	public static final int Socket_Send_Fail = 2;
	public static final int Socket_Send_OK = 3;
	public static final int Socket_TCP_TIMEOUT = 4;
	

	//APP����
	/**�û�����ע���*/
	public static final int APP_USER_NAME_REGISTERED				= 0x10000001;	
	/**�û�������ע���*/
	public static final int APP_USER_EMAIL_REGISTERED				= 0x10000002;
	/**�û�������ע��*/
	public static final int APP_USER_NOT_REGISTER					= 0x10000003;
	/**�������*/
	public static final int APP_PASSWORD_ERROR						= 0x10000004;
	/**����ID������*/
	public static final int APP_PLUG_ID_NOTEXIST					= 0x10000005;
	/**���������ø������û�*/
	public static final int APP_PLUG_BEEN_ASSIGNED					= 0x10000006;
	/**�û������޴˲���*/
	public static final int APP_USER_NO_PLUG     		            = 0x10000007;
	/**��������Ѿ�����*/
	public static final int APP_PLUG_NAME_EXIST					    = 0x10000008;
	/**���Ӷ�ʱ����ʧ��*/
	public static final int APP_PLUG_ADD_TIMER_FAIL  			    = 0x10000009;
	/**�޸Ķ�ʱ����ʧ��*/
	public static final int APP_PLUG_MOD_TIMER_FAIL					= 0x1000000A;
	/**ɾ��ʱ����ʧ��*/
	public static final int APP_PLUG_DEL_TIMER_FAIL 				= 0x1000000B;
	/**���ʱ�޷���*/
	public static final int APP_PLUG_CMD_TIMEOUT   				    = 0x1000000C;
	/**��������*/
	public static final int APP_PLUG_OFFLINE						= 0x1000000D;
	/**���䲻����*/
	public static final int APP_USER_EMAIL_NOTEXIST 				= 0x1000000E;	

	
	/**������д��ݿ�ʧ��*/
	public static final int FAILED_TO_INSERT_DB						= 0x20000001;
	/**������������ݿ�ʧ��*/
	public static final int FAILED_TO_UPDATE_DB						= 0x20000002;
	/**�����������ʼ�����ʧ��*/
	public static final int FAILED_TO_SEND_MAIL						= 0x20000003;	
	
	
	private static ArrayList<CmdDefine> m_App_Server_Response = new ArrayList<CmdDefine>();
	static
	{
		m_App_Server_Response.add(new CmdDefine(ServerErrCodeMgr.ERROR_CODE_USER_NAME_REGISTERED, null, R.string.app_response_user_exist));	
		m_App_Server_Response.add(new CmdDefine(ServerErrCodeMgr.ERROR_CODE_EMAIL_REGISTERED, null, R.string.app_response_email_registered));
		m_App_Server_Response.add(new CmdDefine(ServerErrCodeMgr.ERROR_CODE_USER_NAME_UNREGISTERED, null, R.string.app_response_not_register));
		m_App_Server_Response.add(new CmdDefine(ServerErrCodeMgr.ERROR_CODE_PASSWORD_ERROR, null, R.string.app_response_password_error));
		m_App_Server_Response.add(new CmdDefine(ServerErrCodeMgr.ERROR_CODE_MODULE_ID_UNREGISTERED, null, R.string.app_response_plug_not_exist));
		m_App_Server_Response.add(new CmdDefine(ServerErrCodeMgr.ERROR_CODE_MODULE_ID_REGISTERED, null, R.string.app_response_plug_been_assigned));
		m_App_Server_Response.add(new CmdDefine(APP_USER_NO_PLUG, null, R.string.app_response_user_no_plug));
		m_App_Server_Response.add(new CmdDefine(APP_PLUG_NAME_EXIST, null, R.string.app_response_plug_name_exist));
		m_App_Server_Response.add(new CmdDefine(ServerErrCodeMgr.ERROR_CODE_FAILED_ADD_TIMER, null, R.string.app_response_plug_add_timer_fail));
		m_App_Server_Response.add(new CmdDefine(ServerErrCodeMgr.ERROR_CODE_FAILED_MOD_TIMER, null, R.string.app_response_plug_mod_timer_fail));
		m_App_Server_Response.add(new CmdDefine(ServerErrCodeMgr.ERROR_CODE_FAILED_DEL_TIMER, null, R.string.app_response_plug_del_timer_fail));
		m_App_Server_Response.add(new CmdDefine(ServerErrCodeMgr.ERROR_CODE_MODULE_NOT_ANSWER, null, R.string.app_response_plug_noresponse));
		m_App_Server_Response.add(new CmdDefine(ServerErrCodeMgr.ERROR_CODE_MODULE_IS_OFFLINE, null, R.string.app_response_plug_offline));
		m_App_Server_Response.add(new CmdDefine(APP_USER_EMAIL_NOTEXIST, null, R.string.app_response_pwd_email_error));
		
		m_App_Server_Response.add(new CmdDefine(FAILED_TO_INSERT_DB, null, R.string.server_response_fail_to_insert_db));
		m_App_Server_Response.add(new CmdDefine(FAILED_TO_UPDATE_DB, null, R.string.server_response_fail_to_update_db));
		m_App_Server_Response.add(new CmdDefine(ServerErrCodeMgr.ERROR_CODE_FAILED_SEND_EMAIL, null, R.string.server_response_fail_to_send_pwd_email));
	};
	
	public static int getServerResponse(long responseId) {
		for (CmdDefine define : m_App_Server_Response) {
			if (define.mCommand == responseId) {
				return define.mCommand_Desc_Id;
			}
		}
		
		return 0;
	}
}
