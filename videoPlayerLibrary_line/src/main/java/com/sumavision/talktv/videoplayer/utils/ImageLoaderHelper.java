package com.sumavision.talktv.videoplayer.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageLoaderHelper {

	private ImageLoader imageLoader = ImageLoader.getInstance();

	public void loadImage(ImageView imageView, String url, int defalutPic) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(defalutPic)
				.showImageForEmptyUri(defalutPic).showImageOnFail(defalutPic)
				.cacheInMemory(true).cacheOnDisk(true).build();
		imageLoader.displayImage(url, imageView, options, animateFirstListener);
	}
	public void loadImage(ImageView imageView, String url, int defalutPic,ImageLoadingListener listener){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(defalutPic)
				.showImageForEmptyUri(defalutPic).showImageOnFail(defalutPic)
				.cacheInMemory(true).cacheOnDisk(true).build();
		imageLoader.displayImage(url, imageView, options, listener);
	}

	private static final int animationDuration = 600;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	public static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				imageView.setImageBitmap(loadedImage);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, animationDuration);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
