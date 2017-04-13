package com.sumavision.talktv2.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.utils.ImageLoaderHelper;

/**
 * fragment基类：子类采用单例模式
 * 
 * @author suma-hpb
 * 
 */
public abstract class BaseDialogFragment extends DialogFragment implements
		OnHttpErrorListener {
	protected Activity mActivity;
	private ImageLoaderHelper imageLoaderHelper;
	protected LayoutInflater inflater;
	protected String lastUpdate;
	protected static final String RES_ID = "resId";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	public void onDetach() {
		super.onDetach();
		mActivity = null;
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		resId = getArguments() != null ? getArguments().getInt("resId") : 0;

	}

	/**
	 * init view
	 * 
	 * @param view
	 */
	protected abstract void initViews(View view);

	protected View rootView;
	int resId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (resId == 0) {
			String message = " must set the layout resouce before use";
			throw new IllegalStateException(message);
		}
		if (rootView == null) {
			rootView = inflater.inflate(resId, null);
			imageLoaderHelper = new ImageLoaderHelper();
			this.inflater = inflater;
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
			lastUpdate = getString(R.string.last_update, sdf.format(new Date()));
			initViews(rootView);
		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
		return rootView;
	}

	@Override
	public void onError(int code) {
		Toast.makeText(mActivity, "http error", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 图片加载
	 * 
	 * @param imageView
	 * @param url
	 * @param defaultPic
	 */
	protected void loadImage(final ImageView imageView, String url,
			int defaultPic) {
		imageLoaderHelper.loadImage(imageView, url, defaultPic);
	}
}
