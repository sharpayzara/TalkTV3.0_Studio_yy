package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.application.TalkTvApplication;
import com.sumavision.talktv2.bean.ActivityData;
import com.sumavision.talktv2.bean.AddressData;
import com.sumavision.talktv2.bean.DistrictData;
import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.ExchangeEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.GetAddressParser;
import com.sumavision.talktv2.http.json.GetAddressRequest;
import com.sumavision.talktv2.http.json.ModifyAddressRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.utils.PullParser;

import de.greenrobot.event.EventBus;

/**
 * 填写收货地址页面
 * */
public class AddressActivity extends BaseActivity implements OnClickListener {
	
	private boolean modify;
	
	private EditText nameEdit;
	private EditText phoneEdit;
	private EditText addressEdit;
	private EditText codeEdit;
	private ImageButton nameIbt;
	private ImageButton phoneIbt;
	private ImageButton addressIbt;
	private ImageButton codeIbt;
	private TextView provinceTxt;
	private TextView cityTxt;
	private TextView districtTxt;
	private Button submit;
	private RelativeLayout chooseProvince;
	private RelativeLayout chooseCity;
	private RelativeLayout chooseDistrict;
	
	private int pid = -1;
	private int cid = -1;
	
	private ArrayList<DistrictData> pList;
	private SparseArray<ArrayList<DistrictData>> cList;
	private SparseArray<ArrayList<DistrictData>> dList;
	
	private String[] strArr;
	private AddressData addressData;
	
	private int clickIndex;
	private boolean fromExchange;
	private boolean fromBadgeFlow;
	private ActivityData mActivityNewData;
	private ExchangeGood giftDataInfo = new ExchangeGood();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("地址");
		setContentView(R.layout.activity_address_info);
		
		getExtras();
		initViews();
		initListener();
	}
	
	private void getExtras() {
		Intent intent = getIntent();
		modify = intent.getBooleanExtra("modify", false);
		addressData = ((TalkTvApplication) getApplication()).mAddressData;
		if (addressData == null) {
			addressData = new AddressData();
		}
		pList = PullParser.pList;
		cList = PullParser.cList;
		dList = PullParser.dList;
		
		clickIndex = intent.getIntExtra("clickIndex", -1);
		giftDataInfo.userGoodsId = intent.getLongExtra("userGoodsId", 0);
		giftDataInfo.type = intent.getIntExtra("goodsType", 0);
		giftDataInfo.status = intent.getIntExtra("status", 0);
		giftDataInfo.ticket = intent.getBooleanExtra("ticket", false);
		fromExchange = intent.getBooleanExtra("exchangePage", false);

		fromBadgeFlow = intent.getBooleanExtra("fromZhongjiang", false);
		if (fromBadgeFlow) {
			mActivityNewData = new ActivityData();
			mActivityNewData.playPic = intent.getStringExtra("playPic");
			mActivityNewData.activityPic = intent.getStringExtra("activityPic");
			mActivityNewData.activityName = intent
					.getStringExtra("activityName");
			mActivityNewData.videoPath = intent.getStringExtra("videoPath");
			mActivityNewData.activityId = intent.getLongExtra("activityId", 0L);
		}
	}
	
	private void initViews() {
		initLoadingLayout();
		hideLoadingLayout();
		nameEdit = (EditText) findViewById(R.id.name_edit);
		phoneEdit = (EditText) findViewById(R.id.phone_edit);
		addressEdit = (EditText) findViewById(R.id.address_edit);
		codeEdit = (EditText) findViewById(R.id.code_edit);
		nameIbt = (ImageButton) findViewById(R.id.name_ibt);
		phoneIbt = (ImageButton) findViewById(R.id.phone_ibt);
		addressIbt = (ImageButton) findViewById(R.id.address_ibt);
		codeIbt = (ImageButton) findViewById(R.id.code_ibt);
		provinceTxt = (TextView) findViewById(R.id.province_edit);
		cityTxt = (TextView) findViewById(R.id.city_edit);
		districtTxt = (TextView) findViewById(R.id.district_edit);
		submit = (Button) findViewById(R.id.submit);
		chooseProvince = (RelativeLayout) findViewById(R.id.choose_province);
		chooseCity = (RelativeLayout) findViewById(R.id.choose_city);
		chooseDistrict = (RelativeLayout) findViewById(R.id.choose_district);
		if (modify) {
			nameEdit.setText(addressData.name);
			phoneEdit.setText(addressData.phone);
			addressEdit.setText(addressData.street);
			codeEdit.setText(addressData.code);
			provinceTxt.setText(addressData.province);
			cityTxt.setText(addressData.city);
			districtTxt.setText(addressData.district);
			nameIbt.setVisibility(View.VISIBLE);
			phoneIbt.setVisibility(View.VISIBLE);
			addressIbt.setVisibility(View.VISIBLE);
			codeIbt.setVisibility(View.VISIBLE);
			pid = getId(pList, addressData.province);
			cid = getId(cList.get(pid), addressData.city);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		EventBus.getDefault().post(new ExchangeEvent());
		return super.onOptionsItemSelected(item);
	}
	
	private void initListener() {
		chooseProvince.setOnClickListener(this);
		chooseCity.setOnClickListener(this);
		chooseDistrict.setOnClickListener(this);
		submit.setOnClickListener(this);
		nameIbt.setOnClickListener(this);
		addressIbt.setOnClickListener(this);
		phoneIbt.setOnClickListener(this);
		codeIbt.setOnClickListener(this);
		nameEdit.addTextChangedListener(new InputTextWatcher(nameIbt));
		phoneEdit.addTextChangedListener(new InputTextWatcher(phoneIbt));
		addressEdit.addTextChangedListener(new InputTextWatcher(addressIbt));
		codeEdit.addTextChangedListener(new InputTextWatcher(codeIbt));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.choose_province:
			if (pList != null) {
				ListDialog(0, -1);
			}
			break;
		case R.id.choose_city:
			if (pid != -1 && cList != null) {
				ListDialog(pid, 1);
			} else {
				Toast.makeText(this, "请先选择省份", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.choose_district:
			if (cid != -1 && dList != null) {
				ListDialog(cid, 2);
			} else {
				Toast.makeText(this, "请先选择城市", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.submit:
			showLoadingLayout();
			request();
			break;
		case R.id.name_ibt:
			setCancel(nameIbt, nameEdit);
			break;
		case R.id.phone_ibt:
			setCancel(phoneIbt, phoneEdit);
			break;
		case R.id.address_ibt:
			setCancel(addressIbt, addressEdit);
			break;
		case R.id.code_ibt:
			setCancel(codeIbt, codeEdit);
			break;
		}
	}
	
	private void setExtras(Intent intent) {
		intent.putExtra("clickIndex", clickIndex);
		intent.putExtra("userGoodsId", giftDataInfo.userGoodsId);
		intent.putExtra("goodsType", giftDataInfo.type);
		intent.putExtra("status", giftDataInfo.status);
		intent.putExtra("exchangePage", fromExchange);
		intent.putExtra("fromZhongjiang", fromBadgeFlow);
		intent.putExtra("ticket", giftDataInfo.ticket);
		if (mActivityNewData != null) {
			intent.putExtra("playPic", mActivityNewData.playPic);
			intent.putExtra("activityPic", mActivityNewData.activityPic);
			intent.putExtra("activityName", mActivityNewData.activityName);
			intent.putExtra("activityId", mActivityNewData.activityId);
			intent.putExtra("videoPath", mActivityNewData.videoPath);
		}
	}
	
	private void setDataSource(ArrayList<DistrictData> list, int area) {
		if (list != null && list.size() > 0) {
			strArr = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				strArr[i] = list.get(i).name;
			}
		} else {
			if (area == -1) {
				return;
			} else if (area == 1) {
				strArr = new String [] {provinceTxt.getText().toString()};
				cid = 0;
			} else {
				strArr = new String [] {cityTxt.getText().toString()};
			}
		}
	}
	
	private void ListDialog(final int id, final int area) {
		if (area == -1) {
			setDataSource(pList, area);
		} else {
			if (area == 1) {
				setDataSource(cList.get(id), area);
			} else {
				setDataSource(dList.get(id), area);
			}
		}
		new AlertDialog.Builder(this).setTitle("请选择")
		.setItems(strArr, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = strArr[which];
				if (area == -1) {
					pid = pList.get(which).id;
					provinceTxt.setText(name);
					cityTxt.setText("");
					districtTxt.setText("");
					cid = -1;
				} else if (area == 1) {
					if (cList != null && cList.get(id) != null) {
						cid = cList.get(id).get(which).id;
					}
					cityTxt.setText(name);
					districtTxt.setText("");
				} else {
					districtTxt.setText(name);
				}
			}
		}).show();
	}
	
	ResultParser rparser = new ResultParser();
	private void request() {
		if (isDataEmpty()) {
			hideLoadingLayout();
			Toast.makeText(this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
			return;
		} else {
		VolleyHelper.post(new ModifyAddressRequest(addressData).make(), new ParseListener(rparser) {
					@Override
					public void onParse(BaseJsonParser parser) {
						hideLoadingLayout();
						if (rparser.errCode == JSONMessageType.SERVER_CODE_ERROR) {
							if (!TextUtils.isEmpty(rparser.errMsg)) {
								Toast.makeText(AddressActivity.this, rparser.errMsg, Toast.LENGTH_SHORT).show();
							}
						} else {
							((TalkTvApplication) getApplication()).mAddressData = addressData;
							Intent intent = new Intent(AddressActivity.this, RealGoodsActivity.class);
							checkAddressRequest();
							setExtras(intent);
							startActivity(intent);
							finish();
						}
					}
				}, null);
		}
	}
	
	private boolean isDataEmpty() {
		boolean empty = TextUtils.isEmpty(phoneEdit.getText().toString())
				|| TextUtils.isEmpty(nameEdit.getText().toString())
				|| TextUtils.isEmpty(addressEdit.getText().toString())
				|| TextUtils.isEmpty(codeEdit.getText().toString())
				|| TextUtils.isEmpty(provinceTxt.getText().toString())
				|| TextUtils.isEmpty(cityTxt.getText().toString())
				|| TextUtils.isEmpty(districtTxt.getText().toString());
		if (!empty) {
			addressData.name = nameEdit.getText().toString();
			addressData.phone = phoneEdit.getText().toString();
			addressData.province = provinceTxt.getText().toString();
			addressData.street = addressEdit.getText().toString();
			addressData.code = codeEdit.getText().toString();
			addressData.district = districtTxt.getText().toString();
			addressData.city = cityTxt.getText().toString();
		}
		return empty;
	}
	
	private void setCancel(ImageButton imgBtn, EditText edit) {
		edit.setText("");
		imgBtn.setVisibility(View.GONE);
	}
	
	private int getId(ArrayList<DistrictData> list, String name) {
		if (list == null || name == null) {
			return -1;
		}
		for (DistrictData d : list) {
			if (d.name.equals(name)) {
				return d.id;
			}
		}
		return -1;
	}
	
	private GetAddressParser aparser = new GetAddressParser();

	public void checkAddressRequest() {
		showLoadingLayout();
		VolleyHelper.post(new GetAddressRequest().make(), new ParseListener(
				aparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				hideLoadingLayout();
				if (aparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					((TalkTvApplication) getApplication()).mAddressData = aparser.address;
				}
			}
		}, null);
	}
	
	private class InputTextWatcher implements TextWatcher {
		
		private ImageButton cancel;
		public InputTextWatcher(ImageButton cancel) {
			this.cancel = cancel;
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			setCancleAvailable(s);
		}

		@Override
		public void afterTextChanged(Editable s) {
			
		}
		
		private void setCancleAvailable(CharSequence s) {
			if (s.length() > 0) {
				cancel.setVisibility(View.VISIBLE);
			} else {
				cancel.setVisibility(View.GONE);
			}
		}
	}
}
