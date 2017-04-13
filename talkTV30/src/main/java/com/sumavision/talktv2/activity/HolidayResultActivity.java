package com.sumavision.talktv2.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.HolidayGoodsDetailParser;
import com.sumavision.talktv2.http.json.HolidayGoodsDetailRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.utils.WebpUtils;

public class HolidayResultActivity extends BaseActivity {
	private TextView resultText,goodsText,wayText,nameText;
	private ImageView img;
	private LinearLayout bottomLayout;
	private RelativeLayout infoLayout;
	private boolean isAward;
	private int goodsId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("我的奖品");
		setContentView(R.layout.activity_holiday_result);
		initViews();
		isAward = getIntent().getBooleanExtra("isAward",false);
		goodsId = getIntent().getIntExtra("goodsId",0);
		if (isAward){
			getResult();
		}else {
			bottomLayout.setVisibility(View.GONE);
			img.setImageBitmap(WebpUtils.getAssetBitmap(this, "webp/thank4join.webp"));
			resultText.setText("数码视讯祝您端午节快乐，阖家欢乐。");
			hideLoadingLayout();
			infoLayout.setVisibility(View.VISIBLE);
		}
	}

	private void initViews() {
		initLoadingLayout();
		resultText = (TextView) findViewById(R.id.result_desc);
		goodsText = (TextView) findViewById(R.id.goods_desc);
		wayText = (TextView) findViewById(R.id.way_desc);
		img = (ImageView) findViewById(R.id.logo);
		bottomLayout = (LinearLayout) findViewById(R.id.result_bottom);
		nameText = (TextView) findViewById(R.id.goods_name);
		infoLayout = (RelativeLayout) findViewById(R.id.info_layout);
	}
	HolidayGoodsDetailParser mParser = new HolidayGoodsDetailParser();
	private void getResult(){
		showLoadingLayout();
		VolleyHelper.post(new HolidayGoodsDetailRequest(goodsId).make(), new ParseListener(mParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (parser.errCode == JSONMessageType.SERVER_CODE_OK){
					hideLoadingLayout();
					infoLayout.setVisibility(View.VISIBLE);
					loadImage(img,mParser.pic,R.drawable.emergency_pic_bg_detail);
					resultText.setText("恭喜您获奖");
					goodsText.setText(mParser.description);
					nameText.setText(mParser.name);
				}else {
					showErrorLayout();
				}
			}
		}, new OnHttpErrorListener() {
			@Override
			public void onError(int code) {
				showErrorLayout();
			}
		});
	}

	@Override
	protected void reloadData() {
		super.reloadData();
		getResult();
	}
}
