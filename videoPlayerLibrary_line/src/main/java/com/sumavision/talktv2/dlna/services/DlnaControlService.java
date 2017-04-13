package com.sumavision.talktv2.dlna.services;

import org.cybergarage.upnp.Device;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.sumavision.itv.lib.dlna.DeviceSearchReceiver;
import com.sumavision.itv.lib.dlna.DlnaCotroller;
import com.sumavision.itv.lib.dlna.engine.DlnaEngine;
import com.sumavision.itv.lib.dlna.engine.UpnpRequest;
import com.sumavision.itv.lib.dlna.listener.OnDeviceSearchListener;
import com.sumavision.itv.lib.dlna.listener.OnDlnaEventListener;
import com.sumavision.itv.lib.dlna.listener.OnThrowErrorListener;
import com.sumavision.itv.lib.dlna.model.DeviceProxy;
import com.sumavision.itv.lib.dlna.model.DlnaData;
import com.sumavision.itv.lib.dlna.model.DlnaGlobal;
import com.sumavision.itv.lib.dlna.model.MediaProgram;
import com.sumavision.itv.lib.dlna.service.DlnaService;
import com.sumavision.itv.lib.dlna.util.UpnpUtil;

/**
 * 设备搜索、dlna操控
 * 
 * @author suma-hpb
 * 
 */
public class DlnaControlService extends Service implements
		OnDeviceSearchListener, OnDlnaEventListener, OnThrowErrorListener {

	private static final String TAG = "DlnaControlService";

	private OnDlnaListener dlnaListener;
	private OnDLNAEventListener eventListener;

	public void setEventListener(OnDLNAEventListener eventListener) {
		this.eventListener = eventListener;
	}

	public void setDlnaListener(OnDlnaListener dlnaListener) {
		this.dlnaListener = dlnaListener;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	public class MyBinder extends Binder {
		public DlnaControlService getService() {
			return DlnaControlService.this;
		}
	}

	public int deviceState = DEVICE_UNFOUND;

	public static void setSelectedDevice(Device dev) {
		DeviceProxy.getInstance().setSelectedDevice(dev);
		DlnaData.current().AVT = UpnpUtil.getAVTService(dev);
		DlnaData.current().CM = UpnpUtil.getCMService(dev);
		DlnaData.current().RCS = UpnpUtil.getRCService(dev);
		DlnaData.current().XCTC = UpnpUtil.getXCTCService(dev);
		if (DlnaGlobal.remote)
			new UpnpRequest().initUpnpServiceRequest(UpnpRequest.GET_RCS);
		else
			DlnaData.current().initDlnaAction();
	}

	/**
	 * search
	 */
	public void search() {
		if (null == searchReceive) {
			searchReceive = new DeviceSearchReceiver(this, this);
			IntentFilter ifilter = new IntentFilter();
			ifilter.addAction(DlnaService.NEW_DEVICES_FOUND);
			ifilter.addAction(DlnaService.NO_DEVICES_FOUND);
			ifilter.addAction(DlnaService.SEARCH_DEVICES_ERROR);
			registerReceiver(searchReceive, ifilter);
		}
		if (deviceState != DEVICE_FOUNDED) {
			deviceState = DEVICE_SEARCHING;
			startService(new Intent(TalkTvDlnaService.SEARCH));
		} else {
			onNewDeviceFound();
		}
	}

	public void play() {
		DlnaCotroller.play(1, this);
	}

	public void pause() {
		DlnaCotroller.pause(this);
	}

	public void stopDlna() {
		DlnaCotroller.stop(this);
	}

	public void seek(String pos, boolean isVod) {
		DlnaCotroller.seek(pos, isVod, this);
	}

	public void setUrl(String currentUri) {
		DlnaCotroller.setTransportUrl(currentUri, this);
	}

	public void setVolume(int vol) {
		DlnaCotroller.setVolmue(vol, this);
	}

	public void getPosition() {
		DlnaCotroller.getPositionInfo(this);
	}

	public void getVlume() {
		DlnaCotroller.getVolume(this);
	}

	private MyBinder myBinder = new MyBinder();

	private DeviceSearchReceiver searchReceive;

	@Override
	public void onCreate() {
		DlnaCotroller.setDlnaEventListener(this);
		DlnaCotroller.setThrowErrorListener(this);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "ondestroy");
		stopService(new Intent(TalkTvDlnaService.SEARCH));
		if (searchReceive != null) {
			unregisterReceiver(searchReceive);
		}
	}

	public static final int DEVICE_FOUNDED = 1;
	public static final int DEVICE_UNFOUND = 2;
	public static final int DEVICE_SEARCHING = 3;

	@Override
	public void onNewDeviceFound() {
		Log.e(TAG, "onNewDeviceFound");
		deviceState = DEVICE_FOUNDED;
		if (dlnaListener != null) {
			dlnaListener.searchedNewDevice();
		}
	}

	@Override
	public void onNoDeviceFound() {
		deviceState = DEVICE_UNFOUND;
		Log.e(TAG, "noDeviceFound");
		if (dlnaListener != null) {
			dlnaListener.searchDeviceError();
		}

	}

	@Override
	public void onSearchDeviceError() {
		deviceState = DEVICE_UNFOUND;
		Log.e(TAG, "onSearchDeviceError");
		if (dlnaListener != null) {
			dlnaListener.searchDeviceError();
		}
	}

	@Override
	public void eventDlnaGetPositionInfo(boolean isOk, String absTime,
			String realTime, String trackDuration) {
		if (eventListener != null) {
			eventListener.getPosition(isOk, absTime, realTime, trackDuration);
		}
	}

	@Override
	public void throwError(int errorCode) {

	}

	@Override
	public void eventCallback(int eventType, boolean isOk, String result) {
		if (eventListener == null) {
			return;
		}
		switch (eventType) {
		case DlnaEngine.EVENT_DLNA_ACTION_SETTRANSPORTURL:
			eventListener.eventSetUrl(isOk);
			break;
		case DlnaEngine.EVENT_DLNA_ACTION_SEEK:
			eventListener.eventSeek(isOk);
			break;
		case DlnaEngine.EVENT_DLNA_ACTION_GETVOLUME:
			eventListener.eventGetVolume(isOk, result);
		case DlnaEngine.EVENT_DLNA_ACTION_PLAY:
			eventListener.eventPlay(isOk);
			break;
		case DlnaEngine.EVENT_DLNA_ACTION_PAUSE:
			eventListener.eventPause(isOk);
			break;
		case DlnaEngine.EVENT_DLNA_ACTION_STOP:
			eventListener.eventStop(isOk);
			break;
		case DlnaEngine.EVENT_DLNA_ACTION_SETVOLMUE:
			eventListener.eventSetVolume(isOk);
			break;
		default:
			break;
		}

	}

	@Override
	public void eventDlnaGetMediaInfo(boolean isOk, MediaProgram program) {

	}

}
