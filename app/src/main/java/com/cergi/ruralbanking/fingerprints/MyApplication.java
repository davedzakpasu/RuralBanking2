package com.cergi.ruralbanking.fingerprints;

import com.cergi.ruralbanking.fingerprints.logic.BluetoothChatService;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class MyApplication extends Application implements BluetoothChatService.OnConnectListener{
	private BluetoothChatService chatService = null;

	private boolean isConnect;

	private HandlerThread handlerThread;
	private static final int CONNECT_SUCCESS = 1;
	
	private static final int CONNECT_FAIL = 2;
	
	private static final int CONNECT_LOST = 3;

	@Override
	public void onCreate() {
		super.onCreate();
		handlerThread = new HandlerThread("handlerThread");
		handlerThread.start();
	}
	
	public HandlerThread getHandlerThread() {
		return handlerThread;
	}

	public BluetoothChatService getChatService() {
		return chatService;
	}

	public void setChatService(BluetoothChatService mChatService) {
		this.chatService = mChatService;
	}
	
	
	
	public boolean isConnect() {
		return isConnect;
	}

	public void setConnect(boolean isConnect) {
		this.isConnect = isConnect;
		Log.i("whw", "isConnect="+isConnect);
	}

	
	@Override
	public void onConnectSuccess() {
		setConnect(true);
		Log.i("whw", "onConnectSuccess");
		mHandler.sendEmptyMessage(CONNECT_SUCCESS);
	}

	@Override
	public void onConnectFail() {
		setConnect(false);
		Log.i("whw", "onConnectFail");
		mHandler.sendEmptyMessage(CONNECT_FAIL);
	}

	@Override
	public void onConnectLost() {
		setConnect(false);
		Log.i("whw", "onConnectLost");
		mHandler.sendEmptyMessage(CONNECT_LOST);
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent intent = new Intent(ConnectActivity.CONNECT_RESULT);
			switch (msg.what) {
			case CONNECT_SUCCESS:
				Log.i("whw", "CONNECT_SUCCESS");
				//ToastUtil.showToast(MyApplication.this, R.string.connection_success);
				intent.putExtra("result", 1);
				sendBroadcast(intent);
				break;
			case CONNECT_FAIL:
				Log.i("whw", "CONNECT_FAIL");
				//ToastUtil.showToast(MyApplication.this, R.string.connection_fail);
				intent.putExtra("result", 2);
				sendBroadcast(intent);
				break;
			case CONNECT_LOST:
				Log.i("whw", "CONNECT_Lost");
				//ToastUtil.showToast(MyApplication.this, R.string.connection_lost);
				intent.putExtra("result", 3);
				sendBroadcast(intent);
				break;
			default:
				break;
			}
		}
		
	};


}
