package com.thingzdo.ui;

public class CmdDefine {
	public int    mCommand;       //命令码
	public String mCommand_Param; //命令参数
	public int    mCommand_Desc_Id; //命令中文
	
	public CmdDefine(int command, String param, int commandResId) {
		mCommand = command;
		mCommand_Param = param;
		mCommand_Desc_Id = commandResId;
	}
}
