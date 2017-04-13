package com.sumavision.talktv2.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.AppData;
import com.sumavision.talktv2.service.AppDownloadService;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 应用推荐适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class RecommandAppAdapter extends IBaseAdapter<AppData> {

	private Context context;

	public RecommandAppAdapter(Context context, List<AppData> objects) {
		super(context, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_recommend_app, null);
		}
		ImageView pic = ViewHolder.get(convertView, R.id.pic);
		ImageView installView = ViewHolder.get(convertView, R.id.install);
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView intro = ViewHolder.get(convertView, R.id.intro);
		final AppData temp = getItem(position);
		String name = temp.name;
		if (name != null) {
			nameTxt.setText(name);
		}
		if (temp.shortIntro != null) {
			intro.setText(temp.shortIntro);
		}
		String url = temp.pic;
		loadImage(pic, url, R.drawable.pd_program_pic);
		final boolean isInstalled = checkIntall(temp);
		if (isInstalled) {
			installView.setBackgroundResource(R.drawable.app_start);
		} else {
			installView.setBackgroundResource(R.drawable.app_install);
		}
		installView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isInstalled) {
					// String url = temp.url;
					// String title = temp.name;
					// openWebViewActivity(url, title);
					// AutoNetConnection.count = 0;
					// AutoNetConnection.processPlayURL(url);
					new AlertDialog.Builder(context)
							.setNegativeButton("取消", null)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (!temp.url.endsWith(".apk")) {
												Intent intent = new Intent();
												intent.setAction("android.intent.action.VIEW");
												Uri content_url = Uri.parse(temp.url);
												intent.setData(content_url);
												context.startActivity(intent);
											} else {
												Intent intent = new Intent(context, AppDownloadService.class);
												intent.putExtra("url", temp.url);
												intent.putExtra("name", temp.name);
												intent.putExtra("appId", (int) temp.id);
												intent.putExtra("resId", R.drawable.icon_small);
												context.startService(intent);
											}
										}

									}).setMessage("下载" + temp.name + "感受更多精彩？")
							.setTitle("更多").create().show();
				} else {
					PackageManager packageManager = context.getPackageManager();
					Intent intent = new Intent();
					intent = packageManager
							.getLaunchIntentForPackage(temp.packageName);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					context.startActivity(intent);

				}

			}
		});
		return convertView;
	}

	private boolean checkIntall(AppData data) {
		if (TextUtils.isEmpty(data.packageName))
			return false;
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(data.packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			if (info != null) {
				return true;
			}
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
}
