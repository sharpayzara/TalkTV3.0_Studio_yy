package com.sumavision.talktv2.utils;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.application.TalkTvApplication;
import com.sumavision.talktv2.components.RoundProgressBar;

/**
 * 图片加载：缓存
 * 
 * @author suma-hpb
 * 
 */
public class ImageLoaderHelper {

	private ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
			.cacheInMemory(true);

	public void loadImage(ImageView imageView, String url, int defalutPic) {
		builder.cacheOnDisk(false).cacheInMemory(true).showImageOnLoading(defalutPic)
				.showImageForEmptyUri(defalutPic).showImageOnFail(defalutPic);
		url = fillUrl(url);
		imageView.setTag(R.id.tag_url, url);
		imageLoader.displayImage(url, imageView, builder.build());
	}

	public void loadImageCacheDisk(ImageView imageView, String url,
			int defalutPic) {
		url = fillUrl(url);
		imageView.setTag(R.id.tag_url, url);
		builder.cacheOnDisk(true).cacheInMemory(true).showImageOnLoading(defalutPic)
				.showImageForEmptyUri(defalutPic).showImageOnFail(defalutPic);
		imageLoader.displayImage(url, imageView, builder.build());
	}

	public void loadImage(ImageView imageView, String url, int defalutPic,
			OnImageLoadingListener listener) {
		url = fillUrl(url);
		imageView.setTag(R.id.tag_url, url);
		builder.cacheOnDisk(false).cacheInMemory(true).showImageOnLoading(defalutPic)
				.showImageForEmptyUri(defalutPic).showImageOnFail(defalutPic);
		imageLoader.displayImage(url, imageView, builder.build(),
				new AnimateFirstDisplayListener(listener));
	}

	/**
	 * listview 快速滑动时停止加载
	 * 
	 * @param listView
	 */
	public void applyScrollListener(ListView listView, OnScrollListener listener) {
		if (listView != null) {
			listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,
					false, true, listener));
		}
	}

	public void loadImageWithProgress(ImageView imageView, String url,
			int defalutPic, final RoundProgressBar progressBar,
			final OnImageLoadingListener listener) {
		url = fillUrl(url);
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(defalutPic)
				.showImageForEmptyUri(defalutPic).showImageOnFail(defalutPic)
				.cacheInMemory(true).cacheOnDisk(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		imageView.setTag(R.id.tag_url, url);
		imageLoader.displayImage(url, imageView, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						progressBar.setProgress(0);
						progressBar.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						progressBar.setVisibility(View.GONE);
						if (listener != null) {
//							Bitmap thumb = ThumbnailUtils.extractThumbnail(
//									loadedImage, 50, 50);
							// BitmapUtils.saveBitmap(thumb, "/TVFan/temp",
							// "share");
						}
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {
						progressBar.setProgress(Math.round(100.0f * current
								/ total));
					}
				});
	}

	private static final int animationDuration = 600;

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		OnImageLoadingListener listener;

		public AnimateFirstDisplayListener(OnImageLoadingListener listener) {
			this.listener = listener;
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				if (listener != null) {
					listener.onLoadingFinish(loadedImage);
				}
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				String tag = (String) imageView.getTag(R.id.tag_url);
				if (tag != null && tag.equals(imageUri)) {
					imageView.setImageBitmap(loadedImage);
				}
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, animationDuration);
					displayedImages.add(imageUri);
				}

			}
		}
	}

	/**
	 * 图片加载监听
	 * 
	 * @author suma-hpb
	 * 
	 */
	public interface OnImageLoadingListener {
		public void onLoadingFinish(Bitmap loadedImage);
	}

	public File getDiskCaheImage(String imageUrl) {
		if (diskCache != null) {
			return diskCache.get(imageUrl);
		}
		return null;
	}

	static UnlimitedDiscCache diskCache = null;

	public static void initImageLoader(Context context) {
		int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);

		MemoryCache memoryCache;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
			memoryCache = new LruMemoryCache(memoryCacheSize);
		} else {
			memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
		}

		if (CommonUtils.externalMemoryAvailable()) {
			diskCache = new UnlimitedDiscCache(new File(
					CommonUtils.getImagCacheDir(context)));
		}
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(memoryCache).denyCacheImageMultipleSizesInMemory()
				.tasksProcessingOrder(QueueProcessingType.LIFO);
		if (diskCache != null) {
			builder.diskCache(diskCache);
		}
		L.writeLogs(false);
		ImageLoader.getInstance().init(builder.build());
	}
	
	private String fillUrl(String url)
	{
		if (!TextUtils.isEmpty(url)) {
			if (!(url.endsWith(".png") || url.endsWith(".jpg"))) {
				url += Constants.PIC_SUFF;
			}
			if (!url.startsWith("http:")) {
				url = Constants.picUrlFor + url;
			}
		}
		return url;
	}
}
