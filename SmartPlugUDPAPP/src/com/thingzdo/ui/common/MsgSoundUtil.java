package com.thingzdo.ui.common;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

public class MsgSoundUtil {
	private Context mContext = null;
    private Timer mSoundTimer = null;
    private Object mToneGeneratorLock = null;
    protected ToneGenerator mToneGenerator = null;
    private boolean mDelayAlert = false;
    private int mAlertCounter = 0;
    private static Handler mAutoDelayHandler = null;
    
	private long mCommand;
	private String mSMS;
	private ArrayList<String> mParamsArray;    
    
	protected static final int TONE_RELATIVE_TYPE = AudioManager.STREAM_NOTIFICATION;
	protected static final int TONE_RELATIVE_VOLUME = 100;
	protected static final int TONE_IDLE_MS = 100;
	protected static final int TONE_BREAK_MS = 50;
	
	public MsgSoundUtil(Context context) {
		mContext = context;
		mToneGenerator = new ToneGenerator(TONE_RELATIVE_TYPE, TONE_RELATIVE_VOLUME);
		mSoundTimer = new Timer();
		mToneGeneratorLock = new Object();
		mAutoDelayHandler = null;
	}
	
	public static void registerHandle(Handler handler) {
		mAutoDelayHandler = handler;	
	}
    
    public void playOne() {
		synchronized (mToneGeneratorLock) {
			sound(); 
		}     	
    }
    
    public void playContinuous(final long command, String sms, ArrayList<String> paramsArray) {
		mCommand = command;
		mSMS = sms;
		mParamsArray = paramsArray;    	
    	
    	mSoundTimer.schedule(soundTask, 0, 1000); 
    }
    
    private TimerTask soundTask = new TimerTask() {
		@Override
		public void run() {
			synchronized (mToneGeneratorLock) {
			
				sound();
				mAlertCounter++;
				if (mAlertCounter >= PubDefine.MAX_ALERT_TIMES && null != mAutoDelayHandler) {
					mAlertCounter = 0;
					
					Looper.prepare();
					Message msg = new Message();
					msg.what = 100;
					mAutoDelayHandler.sendMessage(msg);
			    	Looper.loop();
				}
			}
			
		}
    };
    
    public void pause() {
    	mDelayAlert = true;	
    	mAlertCounter = 0;
    }
    
    public void stop() {
    	if (null != mSoundTimer) {
    		mSoundTimer.cancel();
    		mAlertCounter = 0;
    	}    	
    }
    
    private void sound() {
		if (null != mToneGenerator) {
			mToneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, TONE_IDLE_MS);
			SystemClock.sleep(TONE_IDLE_MS + TONE_BREAK_MS);
			mToneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, TONE_IDLE_MS);	
			SystemClock.sleep(TONE_IDLE_MS + TONE_BREAK_MS);
			mToneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, TONE_IDLE_MS);
		}    	
    }
}
