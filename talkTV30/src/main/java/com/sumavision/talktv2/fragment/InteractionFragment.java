package com.sumavision.talktv2.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.Animator.AnimatorListener;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator;
import com.actionbarsherlock.internal.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.interactive.GuessOption;
import com.sumavision.talktv2.bean.interactive.InteractiveGuess;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveGuessDetailListener;
import com.sumavision.talktv2.http.request.VolleyInteractionRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ImageLoaderHelper;
import com.sumavision.talktv2.utils.RotateAnimation;
import com.sumavision.talktv2.utils.RotateAnimation.InterpolatedTimeListener;
import com.sumavision.talktv2.utils.WebpUtils;
import com.sumavision.talktv2.widget.CircleImageView;
import com.sumavision.talktv2.widget.HoloCircularProgressBar;

/**
 * 实时互动竞猜页面
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class InteractionFragment extends BaseDialogFragment implements
		OnClickListener, InterpolatedTimeListener,
		OnInteractiveGuessDetailListener {

	private int duration;

	private InteractiveGuess guess;

	private boolean fromHistory;

	private HoloCircularProgressBar mHoloCircularProgressBar;

	RelativeLayout optLayout;

	RelativeLayout resultLayout;

	private RelativeLayout upLay;

	private TextView title, upTxt, downTxt;

	private RelativeLayout downLay;

	private TextView result;

	private CircleImageView advertImageView;

	private TextView resultPoint, upPoint, downPoint, choiceTip;

	private static final int SHOW_OPTION = 1;

	private static final int SHOW_RESULT = 2;

	private boolean showOption;

	private boolean showResult;

	private boolean showChoiceResult;

	private ImageLoaderHelper imageLoader = new ImageLoaderHelper();

	private Handler animHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_OPTION:
				showOption = true;
				break;
			case SHOW_RESULT:
				showResult = true;
				break;
			}
			startAnimation();
		};
	};

	/**
	 * 
	 * @param screenPortrait
	 *            是否竖屏
	 * @param dur
	 * @param fromHistory
	 * @param guess
	 * @param showChoiceResult
	 * @return
	 */
	public static InteractionFragment newInstance(boolean screenPortrait,
			int dur, boolean fromHistory, InteractiveGuess guess,
			boolean showChoiceResult) {
		InteractionFragment fragment = new InteractionFragment();
		Bundle bundle = new Bundle();
		if (screenPortrait) {
			bundle.putInt("resId", R.layout.dialog_interaction);
		} else {
			bundle.putInt("resId", R.layout.dialog_interaction_land);
		}
		bundle.putInt("duration", dur);
		bundle.putSerializable("guess", guess);
		bundle.putBoolean("fromHistory", fromHistory);
		bundle.putBoolean("choice", showChoiceResult);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, R.style.MyDialog);
		if (getArguments() != null) {
			guess = (InteractiveGuess) getArguments().getSerializable("guess");
			duration = getArguments().getInt("duration", 0);
			fromHistory = getArguments().getBoolean("fromHistory", false);
			showChoiceResult = getArguments().getBoolean("choice", false);
		}
	}

	private void getGuessDetail() {
		VolleyInteractionRequest.guessDetail(this, this, guess.id);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (fromHistory) {
			getGuessDetail();
		}
	};

	String typeName;

	public void close() {
		// rootView.startAnimation(AnimationUtils.loadAnimation(getActivity(),
		// R.anim.push_right_out));
		this.dismiss();
	}

	@Override
	protected void initViews(View view) {
		mHoloCircularProgressBar = (HoloCircularProgressBar) rootView
				.findViewById(R.id.holoCircularProgressBar1);
		optLayout = (RelativeLayout) rootView.findViewById(R.id.option_layout);
		resultLayout = (RelativeLayout) rootView
				.findViewById(R.id.result_layout);

		rootView.findViewById(R.id.layout_interaction).setBackground(
				new BitmapDrawable(getResources(), WebpUtils.getAssetBitmap(
						getActivity(), "webp/interaction_background.webp")));
		resultLayout.setBackground(new BitmapDrawable(getResources(), WebpUtils
				.getAssetBitmap(getActivity(), "webp/inter_result.webp")));

		result = (TextView) rootView.findViewById(R.id.result);
		title = (TextView) rootView.findViewById(R.id.title);
		resultPoint = (TextView) rootView.findViewById(R.id.result_point);
		upLay = (RelativeLayout) rootView.findViewById(R.id.up_view);
		upTxt = (TextView) rootView.findViewById(R.id.up);
		upPoint = (TextView) rootView.findViewById(R.id.up_point);
		downLay = (RelativeLayout) rootView.findViewById(R.id.down_view);
		downTxt = (TextView) rootView.findViewById(R.id.down);
		downPoint = (TextView) rootView.findViewById(R.id.down_point);
		advertImageView = (CircleImageView) rootView
				.findViewById(R.id.pic_advert);
		choiceTip = (TextView) rootView.findViewById(R.id.choice_tip);
		upLay.setOnClickListener(this);
		downLay.setOnClickListener(this);
		rootView.findViewById(R.id.close).setOnClickListener(this);
		title.setText(guess.title);
		if (guess.prizeType == InteractiveGuess.PRIZE_TYPE_DIAMOND) {
			typeName = getString(R.string.diamond);
		} else {
			typeName = getString(R.string.point);
		}
		if (!fromHistory) {
			if (null == guess.answerOption && !showChoiceResult) {
				mHoloCircularProgressBar.setProgress(0);
				advertImageView.setVisibility(View.VISIBLE);
				imageLoader.loadImage(advertImageView, guess.picAd,
						R.drawable.aadefault);
				upTxt.setText(guess.option.get(0).name);
				upPoint.setText(String.valueOf(guess.prizeCount) + typeName);
				downPoint.setText(String.valueOf(guess.prizeCount) + typeName);
				resultPoint
						.setText(String.valueOf(guess.prizeCount) + typeName);
				downTxt.setText(guess.option.get(1).name);
				animate();
				animHandler.sendEmptyMessageDelayed(SHOW_OPTION, 2000);
			} else {
				initResult();
			}
		} else {
			choiceTip.setVisibility(View.GONE);
			mHoloCircularProgressBar.setProgress(1);
			advertImageView.setVisibility(View.GONE);
			resultLayout.setVisibility(View.VISIBLE);
			optLayout.setVisibility(View.VISIBLE);
		}

	}

	private void initResult() {
		mHoloCircularProgressBar.setProgress(1);
		advertImageView.setVisibility(View.GONE);
		resultLayout.setVisibility(View.VISIBLE);
		optLayout.setVisibility(View.VISIBLE);
		if (showChoiceResult) {
			choiceTip.setVisibility(View.VISIBLE);
			result.setText(guess.userOption.name);
			resultPoint.setText(String.valueOf(guess.prizeCount) + typeName);
		} else {
			choiceTip.setVisibility(View.GONE);
			if (guess.userOption != null && guess.answerOption != null) {
				if (guess.userOption.id == guess.answerOption.id) {
					result.setText(R.string.success);
					resultPoint.setText(getString(R.string.win_point,
							guess.prizeCount) + typeName);
				} else {
					result.setText(R.string.fail);
					resultPoint.setText(getString(R.string.lose_point,
							guess.prizeCount) + typeName);
				}
			}

		}

	}

	int clickedViewId;

	GuessOption checkOption;

	@Override
	public void onClick(View v) {
		clickedViewId = v.getId();
		if (clickedViewId == R.id.up_view) {
			checkOption = guess.option.get(0);
			animHandler.sendEmptyMessage(SHOW_RESULT);
			if (joinListener != null) {
				joinListener.inTimeJoin(checkOption);
			}
		} else if (clickedViewId == R.id.down_view) {
			checkOption = guess.option.get(1);
			animHandler.sendEmptyMessage(SHOW_RESULT);
			if (joinListener != null) {
				joinListener.inTimeJoin(checkOption);
			}
		} else if (clickedViewId == R.id.close) {
			close();
		}

	}

	public IntimeJoinListener joinListener;

	public void setIntimeJoinListener(IntimeJoinListener joinListener) {
		this.joinListener = joinListener;
	}

	/**
	 * 
	 * @param progressBar
	 * @param progress
	 *            进度
	 * @param duration
	 *            完成动画时间
	 */
	private void animate() {
		ObjectAnimator mProgressBarAnimator = ObjectAnimator.ofFloat(
				mHoloCircularProgressBar, "progress", 1.0f);
		mProgressBarAnimator.setDuration(duration * 1000);
		mProgressBarAnimator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationCancel(final Animator animation) {
			}

			@Override
			public void onAnimationEnd(final Animator animation) {
				mHoloCircularProgressBar.setProgress(1.0f);
				close();
			}

			@Override
			public void onAnimationRepeat(final Animator animation) {
			}

			@Override
			public void onAnimationStart(final Animator animation) {
			}
		});
		mProgressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				mHoloCircularProgressBar.setProgress((Float) animation
						.getAnimatedValue());
			}
		});
		mProgressBarAnimator.start();
	}

	private void startAnimation() {
		RotateAnimation rotateAnim = null;
		float cX = optLayout.getWidth() / 2.0f;
		float cY = optLayout.getHeight() / 2.0f;
		rotateAnim = new RotateAnimation(cX, cY,
				RotateAnimation.ROTATE_INCREASE);
		rotateAnim.setFillAfter(true);
		rotateAnim.setInterpolatedTimeListener(this);
		optLayout.startAnimation(rotateAnim);
	}

	@Override
	public void interpolatedTime(float interpolatedTime) {
		if (interpolatedTime > 0.5f) {
			advertImageView.setVisibility(View.GONE);
			optLayout.setVisibility(View.VISIBLE);
			if (showOption) {
				resultLayout.setVisibility(View.GONE);
				upLay.setVisibility(View.VISIBLE);
				downLay.setVisibility(View.VISIBLE);
				showOption = false;
			} else if (showResult) {
				resultLayout.setVisibility(View.VISIBLE);
				upLay.setVisibility(View.GONE);
				downLay.setVisibility(View.GONE);
				if (clickedViewId == R.id.up_view) {
					result.setText(upTxt.getText());
				} else {
					result.setText(downTxt.getText());
				}
				showResult = false;
			}
		}

	}

	/**
	 * 实时互动参与提交接口
	 * 
	 * @param selectionOption
	 */
	public interface IntimeJoinListener {
		/**
		 * 实时互动参与提交
		 * 
		 * @param selectionOption
		 */
		public void inTimeJoin(GuessOption selectionOption);
	}

	@Override
	public void guessDetailResult(int errCode, InteractiveGuess guess) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			if (!guess.status) {
				if (guess.userWin) {
					result.setText(R.string.success);
					resultPoint.setText(getString(R.string.win_point,
							guess.prizeCount) + typeName);
				} else {
					result.setText(R.string.fail);
					resultPoint.setText(getString(R.string.lose_point,
							guess.prizeCount) + typeName);
				}
			} else {
				choiceTip.setVisibility(View.VISIBLE);
				result.setText(guess.userOption.name);
				resultPoint
						.setText(String.valueOf(guess.prizeCount) + typeName);
			}
		} else {
			Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
			dismiss();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.interactiveGuessDetail);
	}

}
