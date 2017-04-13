package com.sumavision.talktv.videoplayer.activity;

import org.cybergarage.upnp.Device;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.itv.lib.dlna.model.DeviceProxy;
import com.sumavision.talktv.videoplayer.R;
import com.sumavision.talktv2.dlna.DeviceAdapter;
import com.sumavision.talktv2.dlna.ShortDevice;
import com.sumavision.talktv2.dlna.services.DlnaControlService;
import com.sumavision.talktv2.dlna.services.OnDlnaListener;
import com.sumavision.talktv2.utils.PreferencesUtils;

/**
 * 甩屏操作:设备搜索及设备列表显示页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class StbSearchActivity extends Activity implements OnClickListener,
		OnDlnaListener {

	public static final String TAG_ICON = "icon";
	public static final String TAG_PATH = "path";
	private Button back;
	private String path;
	private ListView deviceListView;;
	private RelativeLayout all;
	private LinearLayout searchingTips;
	private LinearLayout searchingLayout;
	private LinearLayout searchingResult;
	private LinearLayout searchingResultNull;

	private DeviceAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		org.cybergarage.util.Debug.enabled = true;
		org.cybergarage.util.Debug.on();
		setContentView(R.layout.dlna_layout);
		initView();
	}

	int iconResId;

	private void initView() {
		Intent i = getIntent();
		path = i.getStringExtra(TAG_PATH);
		iconResId = i.getIntExtra(TAG_ICON, 0);
		isLivePlay = i.getBooleanExtra("isLivePlay", false);
		deviceListView = (ListView) findViewById(R.id.result_listview);
		TextView txt = (TextView) findViewById(R.id.help_list);
		SpannableStringBuilder spannable = new SpannableStringBuilder(
				getString(R.string.help_list));
		CharacterStyle span1 = new UnderlineSpan();
		spannable.setSpan(span1, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ForegroundColorSpan span2 = new ForegroundColorSpan(Color.argb(0xff,
				0x1a, 0x8c, 0xff));
		spannable.setSpan(span2, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		CharacterStyle span3 = new StyleSpan(android.graphics.Typeface.ITALIC);
		spannable.setSpan(span3, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		txt.setText(spannable);
		txt.setOnClickListener(this);
		searchingTips = (LinearLayout) findViewById(R.id.search_layout);
		searchingLayout = (LinearLayout) findViewById(R.id.searching_layout);
		searchingLayout.setVisibility(View.GONE);
		searchingResult = (LinearLayout) findViewById(R.id.search_result_layout);
		searchingResultNull = (LinearLayout) findViewById(R.id.search_no_result);
		searchingResultNull.setVisibility(View.GONE);
		back = (Button) findViewById(R.id.cancel_btn);
		back.setOnClickListener(this);
		all = (RelativeLayout) findViewById(R.id.dlna_layout);
		all.setOnClickListener(this);
		Button cmd_search = (Button) this.findViewById(R.id.search_btn);
		cmd_search.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		setList();
	}

	private AlertDialog checkAppDialog;

	@SuppressWarnings("deprecation")
	protected void dialog() {
		if (checkAppDialog != null) {
			checkAppDialog.dismiss();
			checkAppDialog = null;
		}

		checkAppDialog = new AlertDialog.Builder(this).create();
		if (iconResId > 0) {
			checkAppDialog.setIcon(iconResId);
		}
		checkAppDialog.setTitle(getString(R.string.start_wifi_before_throw));
		checkAppDialog.setButton(getString(R.string.start_wifi),
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						checkAppDialog.dismiss();
						Intent intent = new Intent();
						intent.setAction("android.settings.WIFI_SETTINGS");
						startActivity(intent);
					}

				});
		checkAppDialog.setButton2(getString(R.string.exit),
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						checkAppDialog.dismiss();
						finish();
					}

				});
		checkAppDialog.setCancelable(false);
		checkAppDialog.show();
	}

	boolean startSearch;

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.cancel_btn) {
			finish();
		} else if (v.getId() == R.id.search_btn) {
			startSearch = true;
			handler.sendEmptyMessage(START_SEARCHING);
			if (dlnaControlService != null) {
				dlnaControlService.search();
			} else {
				bindService(new Intent(this, DlnaControlService.class),
						connection, Context.BIND_AUTO_CREATE);
			}
		} else if (v.getId() == R.id.help_list) {
			Intent i = new Intent(this, DLNAHelpActivity.class);
			startActivity(i);
			finish();
		}

	}

	private DlnaControlService dlnaControlService;
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			dlnaControlService = ((DlnaControlService.MyBinder) service)
					.getService();
			dlnaControlService.setDlnaListener(StbSearchActivity.this);
			dlnaControlService.search();
		}
	};

	boolean isLivePlay = false;

	private void openDlnaControlActivity(Device device) {
		DlnaControlService.setSelectedDevice(device);
		ShortDevice temp = new ShortDevice();
		temp.name = device.getFriendlyName();
		clearDlnaStatus();
		if (DLNAControllActivity.instance != null) {
			DLNAControllActivity.instance.finish();
		}
		Intent intent = new Intent(this, DLNAControllActivity.class);
		intent.putExtra("playAddress", path);
		intent.putExtra("titleName", temp.name);
		intent.putExtra("breakPoint", getIntent().getIntExtra("breakPoint", 0));
		intent.putExtra("videoDuration",
				getIntent().getLongExtra("videoDuration", 0));
		intent.putExtra("isLivePlay", isLivePlay);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dlnaControlService != null) {
			unbindService(connection);
		}
	}

	boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	private void setList() {
		if (isWifi(this)) {
			if (DeviceProxy.getInstance().getDeviceList().size() > 0) {
				searchingTips.setVisibility(View.VISIBLE);
				searchingResultNull.setVisibility(View.GONE);

				if (DeviceProxy.getInstance().getDeviceList().size() == 1) {
					openDlnaControlActivity(DeviceProxy.getInstance()
							.getDeviceList().get(0));
				} else {
					searchingResult.setVisibility(View.VISIBLE);
					if (adapter == null) {
						adapter = new DeviceAdapter(this, DeviceProxy
								.getInstance().getDeviceList());
						deviceListView.setAdapter(adapter);
						deviceListView
								.setOnItemClickListener(deviceListItemClick);
					} else {
						adapter.notifyDataSetChanged();
					}
				}

			} else {
				searchingResult.setVisibility(View.GONE);
				if (startSearch) {
					handler.sendEmptyMessage(CLOSE_SEARCHING_RESULT_NULL);
				}
			}

		} else {
			dialog();
		}
	}

	private final int START_SEARCHING = 1;
	private final int STOP_SEARCHING = 2;
	private final int CLOSE_SEARCHING_RESULT_NULL = 3;
	private final int SEARCHING_DURETION = 40000;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_SEARCHING:
				searchingTips.setVisibility(View.GONE);
				searchingLayout.setVisibility(View.VISIBLE);
				sendEmptyMessageDelayed(STOP_SEARCHING, SEARCHING_DURETION);
				break;
			case STOP_SEARCHING:
				searchingLayout.setVisibility(View.GONE);
				setList();
				break;
			case CLOSE_SEARCHING_RESULT_NULL:
				Toast.makeText(getApplicationContext(), "未搜索到可用设备",
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			default:
				break;
			}
		};
	};

	private OnItemClickListener deviceListItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Device device = (Device) parent.getItemAtPosition(position);
			openDlnaControlActivity(device);
		}
	};

	private void clearDlnaStatus() {
		DLNAControllActivity.needShowResumeBtn = false;
		PreferencesUtils.putBoolean(this, null, "isPlaying", false);
		PreferencesUtils.putString(this, null, "url", null);
	}

	@Override
	public void searchedNewDevice() {
		handler.sendEmptyMessage(STOP_SEARCHING);
	}

	@Override
	public void searchDeviceError() {
		handler.sendEmptyMessage(CLOSE_SEARCHING_RESULT_NULL);

	}

}
