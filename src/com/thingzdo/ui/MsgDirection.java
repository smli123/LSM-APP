package com.thingzdo.ui;

public class MsgDirection {
	//定义枚举值
	public enum Direction {
	    CMD_SEND, CMD_RECEIVE;

		public static Direction fromInt(int value) {
			if (CMD_SEND.ordinal() == value) {
				return CMD_SEND;
			}
			if (CMD_RECEIVE.ordinal() == value) {
				return CMD_RECEIVE;
			}
			throw new IllegalArgumentException("Invalid value: " + value);
		}
	}
}
