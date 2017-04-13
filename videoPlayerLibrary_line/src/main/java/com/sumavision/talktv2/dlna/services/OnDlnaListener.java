package com.sumavision.talktv2.dlna.services;

/**
 * dlna回调监听
 * 
 * @author suma-hpb
 * 
 */
public interface OnDlnaListener {

	public void searchedNewDevice();

	public void searchDeviceError();
}
