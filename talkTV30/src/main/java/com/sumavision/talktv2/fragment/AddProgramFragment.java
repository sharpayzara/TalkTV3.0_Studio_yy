package com.sumavision.talktv2.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qr_codescan.MipcaActivityCapture;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.GeneralData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.components.FlowLayout;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.SaoSuccessRequest;
import com.sumavision.talktv2.http.json.ShakeProgramTagParser;
import com.sumavision.talktv2.http.json.ShakeProgramTagRequest;
import com.sumavision.talktv2.http.json.UploadProgramParser;
import com.sumavision.talktv2.http.json.UploadProgramRequest;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.jar.Pack200;

import de.greenrobot.event.EventBus;

/**
 * 摇一摇添加影片
 * 
 * @author cx
 * 
 */
public class AddProgramFragment extends BaseFragment implements OnClickListener{

	private FlowLayout flowLayout;
	private EditText urlInput;
	private EditText nameInput;
	private Button save;
	private ImageView scan;
	
	private ShakeProgramTagParser tagParser = new ShakeProgramTagParser();
	private ArrayList<TextView> listTag = new ArrayList<TextView>();
	private View lastTag = null;
	private int tagId;
	private ColorStateList mColorState;
	private Resources mResources;
	
	private final static int SCANNIN_GREQUEST_CODE = 102;
	private boolean htto_ok;

	public static AddProgramFragment newInstance() {
		AddProgramFragment fragment = new AddProgramFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.shake_add_program);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(getActivity());
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		hideLoadingLayout();
		
		flowLayout = (FlowLayout) view.findViewById(R.id.flow_layout);
		urlInput = (EditText) view.findViewById(R.id.link_input);
		nameInput = (EditText) view.findViewById(R.id.name_input);
		save = (Button) view.findViewById(R.id.save);
		scan = (ImageView) view.findViewById(R.id.scan);
		scan.setOnClickListener(this);
		save.setOnClickListener(this);
		
		mResources = getActivity().getResources();
		mColorState = mResources.getColorStateList(R.color.color_filter_text);
	
		getShakeTag();
	}
	
	private void getShakeTag() {
		showLoadingLayout();
		VolleyHelper.post(new ShakeProgramTagRequest().make(), new ParseListener(tagParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (tagParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					updateView();
					hideLoadingLayout();
				}
			}
		}, null);
	}
	
	private void updateView() {
		int length = tagParser.list.size();
		length = length>8 ? 8:length;
		for (int i = 0; i < length; i++) {
			GeneralData data = tagParser.list.get(i);
			flowLayout.addView(getTagView(data.name, data.id));
		}
	}
	
	private TextView getTagView(String str, int id) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getActivity().getResources().getDisplayMetrics());
		int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getActivity().getResources().getDisplayMetrics());
		params.setMargins(margin, margin, 0, 0);
		TextView text = new TextView(getActivity());
		text.setLayoutParams(params);
		text.setBackgroundResource(R.drawable.shake_tag_bg);
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		text.setPadding(padding, padding, padding, padding);
		text.setGravity(Gravity.CENTER);
		text.setTextColor(mColorState);
		text.setText(str);
		text.setTag(id);
		text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setSelected(!v.isSelected());
				if (lastTag != null && lastTag != v) {
					lastTag.setSelected(false);
				}
				if (v.isSelected()){
					lastTag = v;
				}else{
					lastTag = null;
				}
			}
		});
		listTag.add(text);
		return text;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest("saoSuccess");
		VolleyHelper.cancelRequest("shakeProgramTag");
	}

	@Override
	public void reloadData() {
		
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.save) {
			url = urlInput.getText().toString().trim();
			name = nameInput.getText().toString().trim();
			if (checkInput()) {
				MobclickAgent.onEvent(getActivity(), "yjpbaocun");
				uploadProgram(tagId, url, name);
				play();
			}
		} else if (v.getId() == R.id.scan) {
			MobclickAgent.onEvent(getActivity(), "yjperweima");
			Intent intent = new Intent(getActivity(), MipcaActivityCapture.class);
			getActivity().startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
		}
	}
	
	private String url;
	private String name;
	private String tag;
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode,resultCode,data);
//		switch (requestCode) {
//		case SCANNIN_GREQUEST_CODE:
//			if(resultCode == Activity.RESULT_OK){
//				Bundle bundle = data.getExtras();
//				String result = bundle.getString("result");
//				getScanResult(result);
//			}
//			break;
//			case 101:
////				if (htto_ok && resultCode == Activity.RESULT_OK){
////					getActivity().finish();
////				}else{
////					ToastHelper.showToast(getActivity(),"保存失败，请重试");
////				}
//				if (resultCode == Activity.RESULT_OK){
//					getActivity().finish();
//				}
//				break;
//		}
//	}
	
	public void getScanResult(String result) {
		try {
			JSONObject obj = new JSONObject(result);
			url = obj.optString("url");
			name = obj.optString("title");
			tag = obj.optString("tag");
			setInputData();
			ScanSuccess(tag);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void setInputData() {
		urlInput.setText(url);
		nameInput.setText(name);
	}
	
	private boolean checkInput() {
		if (TextUtils.isEmpty(url)) {
			ToastHelper.showToast(getActivity(),"链接不能为空");
			return false;
		}else if(TextUtils.isEmpty(name)){
			ToastHelper.showToast(getActivity(),"名称不能为空");
			return false;
		}
		return checkSelectedTag();
	}
	
	private boolean checkSelectedTag() {
		for (int i = 0; i < listTag.size(); i++) {
			if (listTag.get(i).isSelected()) {
				tagId = (Integer) listTag.get(i).getTag();
				return true;
			}
		}
		ToastHelper.showToast(getActivity(),"请选择一个标签");
		return false;
	}
	
	private void ScanSuccess(String tag) {
		ResultParser resultParser = new ResultParser();
		VolleyHelper.post(new SaoSuccessRequest(getActivity(), tag).make(), new ParseListener(resultParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
			}
		}, null);
	}
	
	
	UploadProgramParser upParser = new UploadProgramParser();
	private void uploadProgram(int tagId, String url, String name) {
		VolleyHelper.post(new UploadProgramRequest(tagId, url, name).make(), new ParseListener(upParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (upParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					UserNow.current().setTotalPoint(upParser.userInfo.totalPoint, upParser.userInfo.vipIncPoint);
					htto_ok = true;
					EventBus.getDefault().post(new VodProgramData());
				}
			}
		}, null);
	}
	
	private void play() {
		Intent intent = new Intent();
		intent.setClass(getActivity(), PlayerActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("hideFav", false);
		intent.putExtra("playType", 5);
		intent.putExtra(PlayerActivity.INTENT_NEEDAVOID, false);
		intent.putExtra("title", name);
		intent.putExtra("isHalf", false);
		intent.putExtra("id",getRandomId());
		intent.putExtra("subid",getRandomId());
		intent.putExtra("resultCode",101);
		getActivity().startActivityForResult(intent, 101);
	}
	public int getRandomId(){
		int n;
		n = 1 + (int)(Math.random() * 100);
		return n;
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(getActivity());
	}
}
