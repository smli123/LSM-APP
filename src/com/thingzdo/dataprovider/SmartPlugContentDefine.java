package com.thingzdo.dataprovider;

import android.net.Uri;
import android.provider.BaseColumns;

public class SmartPlugContentDefine {

	//�û���Ϊ
	public final static class ControlAction implements BaseColumns{
		//�����ControlAction��Authority
		public static final String AUTHORITY = "com.thingzdo.smartplugctrlactionprovider";
	
	    public static final String ALL_RECORD = "smartplugctrlactions";
	    public static final String ONE_RECORD = "smartplugctrlaction"; 
		
		//�����Content�ṩ�����Uri
        public static final Uri ALL_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ALL_RECORD);
        public static final Uri ONE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ONE_RECORD);	

        //���������¼���ݱ�����
	    public static final String TABLE_NAME ="SmartPlugCtrlActions";
	    
	    //�ֶ�����������
	    public static final String DIRECTION       = "Direction";
        public static final String ACTION_CODE     = "ActionCode";
        public static final String ACTION_STRING   = "ActionString";
	    public static final String ACTION_TIME     = "ActionTime";
	    public static final String RECEIVE_TYPE    = "ReceiveType";
	    public static final String RECEIVE_VALUE   = "ReceiveValue";
	    public static final String RESERVE_1       = "Reserve1";
	    public static final String RESERVE_2       = "Reserve2";
	    public static final String RESERVE_3       = "Reserve3";
    
	    //�� ����ֵ
	    public static final int ID_COLUMN            = 0;
	    public static final int ACTION_DIRECTION     = 1;
        public static final int ACTION_CODE_COLUMN   = 2;
        public static final int ACTION_STRING_COLUMN = 3;
        public static final int ACTION_TIME_COLUMN   = 4;	
        public static final int RECEIVE_TYPE_COLUMN  = 5;
        public static final int RECEIVE_VALUE_COLUMN = 6;	        
        public static final int RESERVE_1_COLUMN     = 7;	
        public static final int RESERVE_2_COLUMN     = 8;	
        public static final int RESERVE_3_COLUMN     = 9;	        
   
        //������ṹ��SQL�ű�
	    public final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("
			+ _ID + " integer primary key autoincrement, "
			+ DIRECTION + " integer default 0,"
			+ ACTION_CODE + " integer default 0,"
			+ ACTION_STRING + " text default '',"
			+ ACTION_TIME + " long default 0,"
			+ RECEIVE_TYPE + " integer default 0,"
			+ RECEIVE_VALUE + " text default '',"
			+ RESERVE_1 + " text default '',"
			+ RESERVE_2 + " text default '',"			
			+ RESERVE_3 + " text default '');";

	    public final static String DEFAULT_SORT_ORDER = _ID + " desc";
    }
	
	//�û���Ϣ
	public final static class User implements BaseColumns{
		public static final String AUTHORITY = "com.thingzdo.smartpluguserprovider";
		
	    public static final String ALL_RECORD = "carguardusers";
	    public static final String ONE_RECORD = "carguarduser"; 
		
		//�����Content�ṩ�����Uri
        public static final Uri ALL_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ALL_RECORD);
        public static final Uri ONE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ONE_RECORD);	

        //����������Ϣ���ݱ�����
	    public static final String TABLE_NAME ="Users";
	    
	    //�ֶ�����������
	    public static final String USER_NAME                = "UserName";
        public static final String USER_PWD                   = "UserPwd";
        public static final String PWD_KEEP                    = "KeepPwd";
        public static final String LOGIN_MODE               = "LoginMode";
        public static final String PWD_FIND_EMAIL      = "EMail";
	    
    
	    //�� ����ֵ
	    public static final int ID_COLUMN                                 = 0;
	    public static final int USER_NAME_COLUMN             = 1;
	    public static final int USER_PWD_COLUMN   		      = 2;
	    public static final int PWD_KEEP_COLUMN                = 3;
	    public static final int LOGIN_MODE_COLUMN           = 4;
	    public static final int PWD_FIND_EMAIL_COLUMN  = 5;
	    
   
        //������ṹ��SQL�ű�
	    public final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("
			+ _ID + " integer primary key autoincrement, "
			+ USER_NAME + " text default '',"
			+ USER_PWD + " text default '',"
			+ PWD_KEEP + " bit default 0,"
			+ LOGIN_MODE + " bit default 0,"
			+ PWD_FIND_EMAIL + " text default '');";
	    
	    
	    public static final String TRIGGER_USER_DELETE ="Trigger_Users";
	    public final static String SQL_TRIGER_USER_DELETE = " CREATE TRIGGER [" + TRIGGER_USER_DELETE + "]" 
	    		+ " AFTER DELETE ON [" + TABLE_NAME + "]"
	    		+ " BEGIN"
	    		+ " delete from " + SmartPlugContent.TABLE_NAME 
	    		+ " where " + SmartPlugContent.USER_NAME + " = old." + USER_NAME + ";"
	    		+ " END;";	
    }	
	
	//������Ϣ
	public final static class SmartPlugContent implements BaseColumns{
		public static final String AUTHORITY = "com.thingzdo.smartplugcontentprovider";
		
	    public static final String ALL_RECORD = "smartplugcontents";
	    public static final String ONE_RECORD = "smartplugcontent";

		//�����Content�ṩ�����Uri
        public static final Uri ALL_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ALL_RECORD);
        public static final Uri ONE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ONE_RECORD);	

        //����������Ϣ���ݱ�����
	    public static final String TABLE_NAME ="SmartPlugs";
	    
	    //�ֶ�����������
	    public static final String USER_NAME           = "UserName";
	    public static final String PLUG_ID             = "PlugId";
	    public static final String PLUG_NAME           = "Name";
	    public static final String PLUG_IP             = "IP";
	    public static final String PLUG_ONLINE         = "Online";
	    public static final String PLUG_STATUS         = "Status"; 
	    public static final String PLUG_FLASHMODE      = "FlashMode";
	    public static final String PLUG_COLOR_R        = "Color_R";
	    public static final String PLUG_COLOR_G        = "Color_G";
	    public static final String PLUG_COLOR_B        = "Color_B";
	    public static final String PLUG_PROTOCOLMODE   = "ProtocolMode";
	    public static final String PLUG_VERSION 	   = "Version";
	    public static final String PLUG_MAC            = "MAC";
	    public static final String PLUG_TYPE           = "ModuleType";
	    public static final String CURTAIN_POSITION    = "CurtainPosition";
	    public static final String RESERVE_2           = "Reserve2";
    
	    //�� ����ֵ
	    public static final int ID_COLUMN                	= 0;
	    public static final int USER_NAME_COLUMN            = 1;
	    public static final int PLUG_ID_COLUMN           	= 2;
	    public static final int PLUG_NAME_COLUMN      		= 3; 
	    public static final int PLUG_IP_COLUMN 				= 4;
        public static final int PLUG_ONLINE_COLUMN          = 5;	
        //public static final int PLUG_POWERON_COLUMN         = 6;	
        //public static final int PLUG_LIGHTON_COLUMN         = 7;
        //public static final int PLUG_USBON_COLUMN           = 8;
        public static final int PLUG_STATUS_COLUMN          = 6;
        public static final int PLUG_FLASHMODE_COLUMN       = 7;
	    public static final int PLUG_COLOR_R_COLUMN         = 8;
	    public static final int PLUG_COLOR_G_COLUMN         = 9;
	    public static final int PLUG_COLOR_B_COLUMN         = 10;
	    public static final int PLUG_PROTOCOLMODE_COLUMN    = 11;
	    public static final int PLUG_VERSION_COLUMN    		= 12;
        public static final int PLUG_MAC_COLUMN           	= 13;
        public static final int PLUG_TYPE_COLUMN           	= 14;
        public static final int CURTAIN_POSITION_COLUMN     = 15;
        public static final int RESERVE_2_COLUMN           	= 16;
   
        //������ṹ��SQL�ű�
	    public final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("
			+ _ID + " integer primary key autoincrement, "
			+ USER_NAME + " text default '',"
			+ PLUG_ID + " text default '',"
			+ PLUG_NAME + " text default '',"
			+ PLUG_IP + " text default '',"
			+ PLUG_ONLINE + " int default 0,"
			//+ PLUG_POWERON + " int default 0,"	
			//+ PLUG_LIGHTON + " int default 0,"
			//+ PLUG_USBON + " int default 0,"
			+ PLUG_STATUS + " int default 0,"
			+ PLUG_FLASHMODE + " int default 0,"
			+ PLUG_COLOR_R + " int default 255,"
			+ PLUG_COLOR_G + " int default 255,"
			+ PLUG_COLOR_B + " int default 255,"
			+ PLUG_PROTOCOLMODE + " int default 1,"
			+ PLUG_VERSION + " text default '',"
			+ PLUG_MAC + " text default '',"
			+ PLUG_TYPE + " text default '',"
			+ CURTAIN_POSITION + " int default 0,"
			+ RESERVE_2 + " text default '');";
	    
	    public static final String TRIGGER_USER_DELETE ="Trigger_Timers";
	    public final static String SQL_TRIGER_TIMER_DELETE = " CREATE TRIGGER [" + TRIGGER_USER_DELETE + "]" 
	    		+ " AFTER DELETE ON [" + TABLE_NAME + "]"
	    		+ " BEGIN"
	    		+ " delete from " + SmartPlugTimer.TABLE_NAME 
	    		+ " where " + SmartPlugTimer.PLUG_ID + " = old." + PLUG_ID + ";"
	    		+ " END;";		    

	    public final static String DEFAULT_SORT_ORDER = _ID + " desc";
    }
	
	//������ʱ����
	public final static class SmartPlugTimer implements BaseColumns{
		public static final String AUTHORITY = "com.thingzdo.smartplugtimerprovider";
		
	    public static final String ALL_RECORD = "smartplugtimers";
	    public static final String ONE_RECORD = "smartplugtimer"; 
		
		//�����Content�ṩ�����Uri
        public static final Uri ALL_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ALL_RECORD);
        public static final Uri ONE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ONE_RECORD);	

        //����������Ϣ���ݱ�����
	    public static final String TABLE_NAME ="SmartPlugsTimers";
	    
	    //�ֶ�����������
	    public static final String TIMER_ID            = "TimerId";
	    public static final String PLUG_ID             = "PlugId";
	    public static final String PLUG_TIMER_TYPE     = "Type";
        public static final String PLUG_TIMER_ENABLE   = "Enable";
	    public static final String PLUG_PERIOD         = "Period";
	    public static final String PLUG_BEGINTIME      = "BeginTime";
	    public static final String PLUG_ENDTIME        = "EndTime";
    
	    //�� ����ֵ
	    public static final int ID_COLUMN                	= 0;
	    public static final int TIMER_ID_COLUMN             = 1;
	    public static final int PLUG_ID_COLUMN           	= 2;
	    public static final int TIMER_TYPE_COLUMN         	= 3;
	    public static final int PLUG_TIMER_ENABLE_COLUMN 	= 4;
	    public static final int PLUG_PERIOD_COLUMN     		= 5;
	    public static final int PLUG_BEGINTIME_COLUMN 		= 6;
	    public static final int PLUG_ENDTIME_COLUMN 		= 7;
   
        //������ṹ��SQL�ű�
	    public final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("
	    	+ _ID + " integer primary key autoincrement, "
	    	+ TIMER_ID + " integer default 0,"
	    	+ PLUG_ID + " text default '',"
	    	+ PLUG_TIMER_TYPE + " integer default 0,"
			+ PLUG_TIMER_ENABLE + " int default 0,"
			+ PLUG_PERIOD + " text default '',"
			+ PLUG_BEGINTIME + " text default '',"
			+ PLUG_ENDTIME + " text default '');";

	    public final static String DEFAULT_SORT_ORDER = _ID + " desc";
    }
	
	public final static class SmartPlugIRScene implements BaseColumns{
		public static final String AUTHORITY = "com.thingzdo.smartplugirsceneprovider";
		
	    public static final String ALL_RECORD = "smartplugirscenes";
	    public static final String ONE_RECORD = "smartplugirscene"; 
		
		//�����Content�ṩ�����Uri
        public static final Uri ALL_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ALL_RECORD);
        public static final Uri ONE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ONE_RECORD);	

        //����������Ϣ���ݱ�����
	    public static final String TABLE_NAME ="SmartPlugsIRScenes";
	    
	    //�ֶ�����������
	    public static final String IRSCENE_ID      		= "IRSceneId";
	    public static final String PLUG_ID             	= "PlugId";
	    public static final String IRSCENE_POWER     	= "Power";
        public static final String IRSCENE_MODE   		= "Mode";
	    public static final String IRSCENE_DIRECTION    = "Direction";
	    public static final String IRSCENE_SCALE      	= "Scale";
	    public static final String IRSCENE_TEMPERATURE  = "Temperature";
	    
	    public static final String IRSCENE_TIME        	= "Time";
	    public static final String IRSCENE_PERIOD       = "Period";
	    public static final String IRSCENE_IRNAME       = "IRName";
	    public static final String IRSCENE_ENABLE       = "Enable";
    
	    //�� ����ֵ
	    public static final int ID_COLUMN                	= 0;
	    public static final int IRSCENE_ID_COLUMN           = 1;
	    public static final int PLUG_ID_COLUMN           	= 2;
	    public static final int IRSCENE_POWER_COLUMN        = 3;
	    public static final int IRSCENE_MODE_COLUMN 		= 4;
	    public static final int IRSCENE_DIRECTION_COLUMN    = 5;
	    public static final int IRSCENE_SCALE_COLUMN 		= 6;
	    public static final int IRSCENE_TEMPERATURE_COLUMN 	= 7;
	    
	    public static final int IRSCENE_TIME_COLUMN 		= 8;
	    public static final int IRSCENE_PERIOD_COLUMN 		= 9;
	    public static final int IRSCENE_IRNAME_COLUMN 		= 10;
	    public static final int IRSCENE_ENABLE_COLUMN 		= 11;

	    //������ṹ��SQL�ű�
	    public final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("
	    	+ _ID + " integer primary key autoincrement, "
	    	+ IRSCENE_ID + " integer default 0,"
	    	+ PLUG_ID + " text default '',"
	    	+ IRSCENE_POWER + " integer default 0,"
			+ IRSCENE_MODE + " integer default 0,"
			+ IRSCENE_DIRECTION + " integer default 0,"
			+ IRSCENE_SCALE + " integer default 0,"
			+ IRSCENE_TEMPERATURE + " integer default 0,"
			+ IRSCENE_TIME + " text default '',"
			+ IRSCENE_PERIOD + " text default '',"
			+ IRSCENE_IRNAME + " text default '',"
			+ IRSCENE_ENABLE + " integer default 0);";

	    public final static String DEFAULT_SORT_ORDER = _ID + " desc";
    }
	
/*	public static final class View_User_SmartPlug implements BaseColumns{
		public static final String AUTHORITY = "com.thingzdo.smartpluguserviewprovider";
		
	    //view name
	    public static final  String VIEW_USER_SMARTPLUG    = "view_user_smartplug";
	    
	    public static final String ALL_RECORD        = "view_user_smartplugs";           
	    public static final String ONE_RECORD_ID     = "view_user_smartplug_id";        
	    
		//define Content's Uri
        public static final Uri ALL_CONTENT_URI     = Uri.parse("content://" + AUTHORITY + "/" + ALL_RECORD);
        public static final Uri ID_CONTENT_URI      = Uri.parse("content://" + AUTHORITY + "/" + ONE_RECORD_ID);	
        
	    //�ֶ�����������
        public static final String USER_NAME           = "UserName";
	    public static final String PLUG_ID             = "PlugId";
	    public static final String PLUG_NAME           = "Name";
        public static final String PLUG_TIMER          = "HasTimer";
	    public static final String PLUG_PERIOD         = "Period";
	    public static final String PLUG_BEGINTIME      = "BeginTime";
	    public static final String PLUG_ENDTIME        = "EndTime";
	    public static final String PLUG_IP             = "IP";
	    public static final String PLUG_ONLINE         = "Online";
	    public static final String PLUG_POWERON        = "PowerOn";
	    public static final String RESERVE_1           = "Reserve1";
	    public static final String RESERVE_2           = "Reserve2";
    
	    //�� ����ֵ
	    public static final int ID_COLUMN                	= 0;
	    public static final int USER_NAME_COLUMN           	= 1;
	    public static final int PLUG_ID_COLUMN           	= 2;
	    public static final int PLUG_NAME_COLUMN      		= 3; 
	    public static final int PLUG_TIMER_COLUMN   		= 4;
	    public static final int PLUG_PERIOD_COLUMN     		= 5;
	    public static final int PLUG_BEGINTIME_COLUMN 		= 6;
	    public static final int PLUG_ENDTIME_COLUMN 		= 7;
	    public static final int PLUG_IP_COLUMN 				= 8;
        public static final int PLUG_ONLINE_COLUMN          = 9;	
        public static final int PLUG_POWERON_COLUMN         = 10;	
        public static final int RESERVE_1_COLUMN           	= 11;	        
        public static final int RESERVE_2_COLUMN           	= 12;
   
	    public static final String VIEW_USER_SMARTPLUG_CREATE = "CREATE VIEW IF NOT EXISTS " + VIEW_USER_SMARTPLUG +" AS select "
	    		+ Contacts.TABLE_CONTACTS + "." + Contacts.ID + " as " + CONTACT_ID + ", " 
	    		+ Contacts.TABLE_CONTACTS + "." + Contacts.NAME + " as " + CONTACT_NAME + ", "
	    		+ Contacts.TABLE_CONTACTS + "." + Contacts.PHOTO + " as " + CONTACT_PHOTO + ", "
	    		+ Contacts.TABLE_CONTACTS + "." + Contacts.ACCOUNT_NAME + " as " + ACCOUNT_NAME + ","
	    		+ Contacts.TABLE_CONTACTS + "." + Contacts.ACCOUNT_TYPE + " as " + ACCOUNT_TYPE + ","
	    		+ Contacts.TABLE_CONTACTS + "." + Contacts.SORT_KEY + " as " + SORT_KEY + ","
	    		+ Raw_Contacts.TABLE_RAW_CONTACTS + "." + Raw_Contacts.PHONE_NUMBER + " as " + CONTACT_PHONE_NUMBER + "," 
	    		+ Raw_Contacts.TABLE_RAW_CONTACTS + "." + Raw_Contacts.PHONE_TYPE + " as " + CONTACT_PHONE_TYPE
	    		+ " from " + Raw_Contacts.TABLE_RAW_CONTACTS 
	    		+ " left join " + Contacts.TABLE_CONTACTS + " on " 
    		    + Raw_Contacts.TABLE_RAW_CONTACTS + "." + Raw_Contacts.CONTACT_ID + " = " + Contacts.TABLE_CONTACTS + "." + Contacts.ID; 
 		
  		
		public final static String DEFAULT_SORT_ORDER = CONTACT_ID + " asc";		
	}*/	
	
	
    
}
