package com.sumavision.talktv2.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.InteractiveDetailActivity;
import com.sumavision.talktv2.activity.MyAccountActivity;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.interactive.GuessResult;
import com.sumavision.talktv2.bean.interactive.InteractiveGuess;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveGuessDetailListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveGuessJoinListener;
import com.sumavision.talktv2.http.request.VolleyInteractionRequest;
import com.sumavision.talktv2.utils.Constants;

/**
 * 下注、竞猜结果页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class GuessDialogFragment extends BaseDialogFragment implements
		OnClickListener, OnInteractiveGuessDetailListener,
		OnInteractiveGuessJoinListener {

	private TextView titleTxt;

	private RadioGroup options;

	private RadioButton topOpt, midOpt, bottomOpt;

	private EditText betNumber;

	private ImageView inputPicType;

	private TextView oddsTxt, countTxt, betType;

	private Button betBtn;

	private RelativeLayout errLayout, content;

	InteractiveGuess guess = new InteractiveGuess();

	private GuessResult guessResult = new GuessResult();

	public static GuessDialogFragment newInstance(int id, boolean userJoin) {
		GuessDialogFragment fragment = new GuessDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.dialog_guess);
		bundle.putInt("id", id);
		bundle.putBoolean("userJoin", userJoin);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			guess.id = getArguments().getInt("id", 0);
			guess.userJoin = getArguments().getBoolean("userJoin", false);
		}
		setStyle(STYLE_NO_TITLE, R.style.MyDialog);
	}

	@Override
	protected void initViews(View view) {
		content = (RelativeLayout) rootView.findViewById(R.id.content);
		titleTxt = (TextView) rootView.findViewById(R.id.title);
		inputPicType = (ImageView) rootView.findViewById(R.id.pic_type);
		options = (RadioGroup) rootView.findViewById(R.id.options);
		topOpt = (RadioButton) rootView.findViewById(R.id.option_top);
		midOpt = (RadioButton) rootView.findViewById(R.id.option_middle);
		bottomOpt = (RadioButton) rootView.findViewById(R.id.option_bottom);
		betNumber = (EditText) rootView.findViewById(R.id.bet_number);
		oddsTxt = (TextView) rootView.findViewById(R.id.odds);
		oddsTxt.setTextColor(Color.rgb(97, 192, 255));
		countTxt = (TextView) rootView.findViewById(R.id.total_count);
		betType = (TextView) rootView.findViewById(R.id.bet_type);
		rootView.findViewById(R.id.pay).setOnClickListener(this);
		betBtn = (Button) rootView.findViewById(R.id.btn_bet);
		betBtn.setOnClickListener(this);
		rootView.findViewById(R.id.close).setOnClickListener(this);
		betBtn.setClickable(false);
		errLayout = (RelativeLayout) rootView.findViewById(R.id.errLayout);
		options.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.option_top:
					checkOptionId = guess.option.get(0).id;
					break;
				case R.id.option_middle:
					checkOptionId = guess.option.get(1).id;
					break;
				case R.id.option_bottom:
					checkOptionId = guess.option.get(2).id;
					break;
				}

			}
		});

	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		getGuessDetail();
	}

	private void showLoadingLayout() {
		errLayout.setVisibility(View.VISIBLE);
	}

	private void getGuessDetail() {
		errLayout.setVisibility(View.VISIBLE);
		content.setVisibility(View.GONE);
		VolleyInteractionRequest.guessDetail(this, this, guess.id);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_bet) {
			if (checkOptionId == -1) {
				Toast.makeText(getActivity(), R.string.no_chose_option,
						Toast.LENGTH_SHORT).show();
				return;
			}
			String Num = betNumber.getText().toString().trim();
			if (Num.isEmpty()) {
				Toast.makeText(getActivity(), R.string.guess_bet_tip,
						Toast.LENGTH_SHORT).show();
				return;
			}
			long bet = Long.parseLong(Num);
			if (guess.prizeType == InteractiveGuess.PRIZE_TYPE_DIAMOND) {
				if (bet > UserNow.current().diamond) {
					Toast.makeText(getActivity(), R.string.no_enough_diamond,
							Toast.LENGTH_SHORT).show();
				} else if (bet == 0) {
					Toast.makeText(getActivity(), R.string.no_less_diamond,
							Toast.LENGTH_SHORT).show();
				} else {
					guessJoin(bet);
				}
			} else {
				if (bet > UserNow.current().totalPoint) {
					Toast.makeText(getActivity(), R.string.no_enough_point,
							Toast.LENGTH_SHORT).show();
				} else if (bet == 0) {
					Toast.makeText(getActivity(), R.string.no_less_point,
							Toast.LENGTH_SHORT).show();
				} else {
					guessJoin(bet);
				}
			}
		} else if (v.getId() == R.id.close) {
			dismiss();
		} else if (v.getId() == R.id.pay) {
			startActivity(new Intent(mActivity, MyAccountActivity.class));
		}
	}

	private int checkOptionId = -1;

	private void guessJoin(long betCount) {
		showLoadingLayout();
		VolleyInteractionRequest.joinGuess(this, this, guess.id, checkOptionId,
				betCount);
	}

	private void waitingResultOrEnd() {
		titleTxt.setText(R.string.your_choice);
		topOpt.setVisibility(View.GONE);
		bottomOpt.setVisibility(View.GONE);
		midOpt.setChecked(true);
		betBtn.setClickable(false);
		betNumber.setEnabled(false);
		betNumber.setBackgroundResource(R.drawable.edt_unenable_bg);
		betNumber.setGravity(Gravity.CENTER);
		betBtn.setText(R.string.waiting_result);
		if (guess.userOption != null) {
			betNumber.setText(String.valueOf(guess.userOption.betAmount));
			midOpt.setText(guess.userOption.name);
			if (guess.status) {
			} else {
				if (guess.userWin) {
					betBtn.setText(R.string.bet_win);
				} else {
					betBtn.setText(R.string.bet_lose);
				}
			}
		} else {
			midOpt.setText(guessResult.userOptionName);
			betNumber.setText(String.valueOf(guessResult.consumeCount));
		}
		if (guess.prizeType == InteractiveGuess.PRIZE_TYPE_DIAMOND) {
			countTxt.setText(String.valueOf(UserNow.current().diamond));
			betType.setText(R.string.diamond);
			inputPicType.setImageResource(R.drawable.diamond);
		} else {
			countTxt.setText(String.valueOf(UserNow.current().totalPoint));
			betType.setText(R.string.point);
			inputPicType.setImageResource(R.drawable.gold);
		}
	}

	@Override
	public void guessDetailResult(int errCode, InteractiveGuess guess) {
		errLayout.setVisibility(View.GONE);
		content.setVisibility(View.VISIBLE);
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			this.guess = guess;
			if (guess.userJoin) {
				// 等待结算--或已结束
				waitingResultOrEnd();
			} else {
				betBtn.setClickable(true);
				titleTxt.setText(guess.title);
				if (guess.option != null && guess.option.size() == 3) {
					topOpt.setText(guess.option.get(0).name);
					midOpt.setText(guess.option.get(1).name);
					bottomOpt.setText(guess.option.get(2).name);
				}
				if (guess.prizeType == InteractiveGuess.PRIZE_TYPE_DIAMOND) {
					countTxt.setText(String.valueOf(UserNow.current().diamond));
					betType.setText(R.string.diamond);
					inputPicType.setImageResource(R.drawable.diamond);
				} else {
					countTxt.setText(String.valueOf(UserNow.current().totalPoint));
					betType.setText(R.string.point);
					inputPicType.setImageResource(R.drawable.gold);
				}
			}

		}
	}

	@Override
	public void JoinGuessResult(int errCode, GuessResult guessResult) {
		errLayout.setVisibility(View.GONE);
		content.setVisibility(View.VISIBLE);
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			// 等待结算
			this.guessResult=guessResult;
			
			Toast.makeText(getActivity(), R.string.bet_succeed,
					Toast.LENGTH_SHORT).show();
			waitingResultOrEnd();
			if (mActivity != null
					&& mActivity instanceof InteractiveDetailActivity) {
				((InteractiveDetailActivity) getActivity()).refreshGuessList();
			}
		} else {
			Toast.makeText(getActivity(), R.string.bet_failed,
					Toast.LENGTH_SHORT).show();
		}

	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.interactiveGuessDetail);
		VolleyHelper.cancelRequest(Constants.interactiveGuessJoin);
	}
}
