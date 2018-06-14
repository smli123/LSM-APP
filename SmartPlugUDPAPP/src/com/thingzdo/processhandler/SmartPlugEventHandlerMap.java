package com.thingzdo.processhandler;

public class SmartPlugEventHandlerMap {
	private int mEvent = -1;
	private SmartPlugEventHandler mHandler = null;
	
	public SmartPlugEventHandlerMap(int event, SmartPlugEventHandler handler){
		mEvent = event;
		mHandler = handler;
	}
	
	public int getEvent(){
		return mEvent;
	}
	
	public SmartPlugEventHandler getHandler(){
		return mHandler;
	}
};
