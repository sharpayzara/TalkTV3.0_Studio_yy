package com.sumavision.talktv2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.Good;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.NewYearGuaEvent;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.ChangeScoreParser;
import com.sumavision.talktv2.http.json.ChangeScoreRequest;
import com.sumavision.talktv2.http.json.activities.FetchActivityGoodsParser;
import com.sumavision.talktv2.http.json.activities.FetchActivityGoodsRequest;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.ImageLoaderHelper;
import com.sumavision.talktv2.utils.PreferencesUtils;

import de.greenrobot.event.EventBus;

/**
 * 节日抽奖奖品页
 * */
public class NewYearPrizeActivity extends Activity implements OnClickListener {
	
	private int type;
	private int id;
	//commen
	private TextView title;
	private Button ensure;
	
	//gift layout
	private TextView picName;
	private ImageView pic;
	
	//score layout
	private TextView earnScore;
	private TextView score;
	private Button cancel;
	
	private Good good;
	private int scoreGot;
	private boolean hasEnoughScore;
	
	private ImageLoaderHelper imageLoaderHelper;
	private ExchangeGood exchangeGood = new ExchangeGood();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getExtras();
		if (type == 1) {
			setContentView(R.layout.gift_layout);
		} else {
			setContentView(R.layout.score_layout);
		}
		getPrize();
		initViews();
	}
	
	private void getExtras() {
		type = getIntent().getIntExtra("type", 0);
		id = getIntent().getIntExtra("id", 0);
		if (type == 1) {
			good = new Good();
			good.id = getIntent().getLongExtra("goodId", 0);
			good.level = getIntent().getIntExtra("goodLevel", 0);
			good.name = getIntent().getStringExtra("goodName");
			good.pic = getIntent().getStringExtra("goodPic");
			good.goodsType = getIntent().getIntExtra("goodType", 0);
			good.point = getIntent().getIntExtra("goodPoint", 0);
		}
	}
	
	private void initViews() {
		imageLoaderHelper = new ImageLoaderHelper();
		title = (TextView) findViewById(R.id.title);
		ensure = (Button) findViewById(R.id.ensure);
		ensure.setOnClickListener(this);
		if (type == 1) {
			picName = (TextView) findViewById(R.id.pic_name);
			pic = (ImageView) findViewById(R.id.pic);
			if (good.level > 0) {
				PreferencesUtils.putInt(this, null, "newyear_good_level", good.level);
				title.setText("恭喜你中奖了，赶快去领奖品吧");
				if (!TextUtils.isEmpty(good.pic) && good.goodsType != 3) {
					picName.setText(good.name);
					loadImage(pic, good.pic, R.drawable.aadefault);
				} else if (good.goodsType == 3) {
					pic.setVisibility(View.GONE);
					picName.setVisibility(View.GONE);
					title.setText("恭喜你获得" + good.name + "，可以直接兑换哦");
					scoreGot = good.point;
					UserNow.current().totalPoint += scoreGot;
					EventBus.getDefault().post(new UserInfoEvent());
				}
			} else {
				title.setText("很可惜您没有中奖，请再接再厉！");
				picName.setVisibility(View.GONE);
				pic.setImageResource(R.drawable.no_prize);
			}
		} else {
			earnScore = (TextView) findViewById(R.id.earn_score);
			score = (TextView) findViewById(R.id.score);
			cancel = (Button) findViewById(R.id.cancel);
			cancel.setOnClickListener(this);
			earnScore.setOnClickListener(this);
			if (UserNow.current().totalPoint >= 20) {
				title.setText("是否兑换20积分来获得抽奖机会？");
				hasEnoughScore = true;
			} else {
				hasEnoughScore = false;
				cancel.setText("如何获取积分");
				title.setText("兑换抽奖机会需20积分，但您已经没有积分可用啦，尝试做任务去赚积分吧");
				earnScore.setVisibility(View.GONE);
			}
			score.setText("当前积分：" + UserNow.current().totalPoint);
		}
	}
	
	FetchActivityGoodsParser gparser = new FetchActivityGoodsParser();
	private void getPrize() {
		if (good != null) {
			VolleyHelper.post(new FetchActivityGoodsRequest(id, good.id, AppUtil.getImei(this)).make(), new ParseListener(gparser) {
				@Override
				public void onParse(BaseJsonParser parser) {
					if (gparser.errCode == JSONMessageType.SERVER_CODE_OK) {
						exchangeGood = gparser.exchangeGood;
					}
				}
			}, null);
		}
	}
	
	ChangeScoreParser scoreparser = new ChangeScoreParser();
	public void changeScore() {
		ensure.setEnabled(false);
		VolleyHelper.post(new ChangeScoreRequest(id).make(), new ParseListener(scoreparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (scoreparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					if (scoreparser.resultCode == 1) {
						Toast.makeText(NewYearPrizeActivity.this, "兑换成功", Toast.LENGTH_SHORT).show();
						UserNow.current().totalPoint -= 20;
						EventBus.getDefault().post(new UserInfoEvent());
						EventBus.getDefault().post(new NewYearGuaEvent());
						score.setText("当前积分：" + UserNow.current().totalPoint);
						finish();
					} else {
						updateScoreView();
					}
					ensure.setEnabled(true);
				} else {
					Toast.makeText(NewYearPrizeActivity.this, "兑换失败", Toast.LENGTH_SHORT).show();
				}
			}
		}, null);
	}
	
	private void updateScoreView() {
		title.setText("兑换抽奖机会需要20积分，但您已经没有积分可用啦，尝试做任务去赚取积分吧~");
		score.setText("当前积分：" + UserNow.current().totalPoint);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.cancel) {
			if (hasEnoughScore) {
				finish();
			} else {
				startActivity(new Intent(this, ShopPointsActivity.class));
			}
		} else if (v.getId() == R.id.ensure) {
			if (type == 1 && good.level > 0 && good.goodsType != 3) {
				Intent intent = new Intent(this, ExchangeLimitActivity.class);
				intent.putExtra("hotGoodsId", exchangeGood.hotGoodsId);
				intent.putExtra("userGoodsId", (int)exchangeGood.userGoodsId);
				intent.putExtra("goodsType", exchangeGood.type);
				startActivity(intent);
				EventBus.getDefault().post(new NewYearGuaEvent());
				finish();
			} else if (type == 1 && good.level == 0) {
				EventBus.getDefault().post(new NewYearGuaEvent());
				finish();
			} else if (type == 1 && good.level > 0 && good.goodsType == 3 ) {
				DialogUtil.updateScoreToast("积分+" + scoreGot);
				EventBus.getDefault().post(new NewYearGuaEvent());
				finish();
			} else if (type == 0) {
				if (!hasEnoughScore) {
					finish();
				} else {
					changeScore();
				}
			}
		} else {
			startActivity(new Intent(this, ShopPointsActivity.class));
		}
	}
	
	protected void loadImage(final ImageView imageView, String url,
			int defaultPic) {
		imageLoaderHelper.loadImage(imageView, url, defaultPic);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
