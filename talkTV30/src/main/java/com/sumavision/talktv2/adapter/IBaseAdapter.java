package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.sumavision.talktv2.components.RoundProgressBar;
import com.sumavision.talktv2.utils.ImageLoaderHelper;

/**
 * 适配器基类
 * 
 * @author suma-hpb
 * 
 * @param <T>
 */
public class IBaseAdapter<T> extends ArrayAdapter<T> {

	public Context context;
	public String TAG;
	public LayoutInflater inflater;
	private ImageLoaderHelper mImageLoaderHelper;

	public IBaseAdapter(Context context, T[] objects) {

		super(context, 0, objects);
		initAdapter(context);
	}

	public IBaseAdapter(Context context, List<T> objects) {
		super(context, 0, objects);
		initAdapter(context);
	}

	private void initAdapter(Context context) {
		this.context = context;
		TAG = getClass().getSimpleName();
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mImageLoaderHelper = new ImageLoaderHelper();
	}

	protected void loadImage(final ImageView imageView, String url,
			int defaultPic) {
		mImageLoaderHelper.loadImage(imageView, url, defaultPic);
	}

	public void loadImageCacheDisk(ImageView imageView, String url,
			int defalutPic) {
		mImageLoaderHelper.loadImageCacheDisk(imageView, url, defalutPic);
	}

	protected void loadImageWithProgress(ImageView imageView, String url,
			int defalutPic, final RoundProgressBar progressBar) {
		mImageLoaderHelper.loadImageWithProgress(imageView, url, defalutPic,
				progressBar, null);
	}
}
