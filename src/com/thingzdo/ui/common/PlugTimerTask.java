package com.thingzdo.ui.common;

public class PlugTimerTask {
	public int mTaskID;
	public boolean arrayDay[];
	public String timePowerOn;
	public String timePowerOff;
    public PlugTimerTask(){
    	mTaskID = 1;
    	arrayDay = new boolean[7];  
    	arrayDay[0] = true;
    	arrayDay[2] = true;
    	timePowerOn = "定时通电：21:10";
    	timePowerOff = "定时断电：22:10";
    }
    public String getDayString(){
    	String str = "每周";
    	StringBuilder sb = new StringBuilder();
    	sb.append(str);
    	for(int i=0;i<arrayDay.length;++i){
    		if (arrayDay[i]){
    			sb.append(i+1);
    			sb.append('/');
    		}
    	}
    	return sb.toString();
    }
    public String getTask(){
    	return timePowerOn + timePowerOff;
    } 
}
