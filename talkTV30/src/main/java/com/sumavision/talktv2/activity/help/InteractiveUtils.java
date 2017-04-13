package com.sumavision.talktv2.activity.help;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.InteractiveHelpActivity;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.interactive.GuessOption;
import com.sumavision.talktv2.bean.interactive.GuessResult;
import com.sumavision.talktv2.bean.interactive.InteractiveActivity;
import com.sumavision.talktv2.bean.interactive.InteractiveGuess;
import com.sumavision.talktv2.fragment.InteractionFragment;
import com.sumavision.talktv2.fragment.InteractionFragment.IntimeJoinListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveGuessJoinListener;
import com.sumavision.talktv2.http.request.VolleyInteractionRequest;
import com.sumavision.talktv2.service.InteractiveIntimeService;
import com.sumavision.talktv2.service.InteractiveIntimeService.InteractiveBinder;
import com.sumavision.talktv2.service.InteractiveIntimeService.OnIntimeGuessListener;
import com.sumavision.talktv2.service.IntimeResultThread;
import com.sumavision.talktv2.service.PlayAudioService;
import com.sumavision.talktv2.utils.WebpUtils;

/**
 * 实时竞猜处理通用类
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class InteractiveUtils implements OnClickListener,
		OnIntimeGuessListener, IntimeJoinListener, OnHttpErrorListener,
		OnInteractiveGuessJoinListener {
	private RelativeLayout interactLayout;

	private ImageView interactImg;

	private ImageView interactTip;

	private Animation interactAnim;

	FragmentActivity mActivity;

	private int activityId;

	private int interactStatus;

	private long delayOver;// 延迟关闭互动view时间(ms)
	boolean islive;

	public InteractiveUtils(FragmentActivity mActivity, int activityId,
			int interactStatus, boolean islive) {
		this.mActivity = mActivity;
		this.islive = islive;
		if (islive) {
			if (activityId > 0) {
				this.activityId = activityId;
				this.interactStatus = interactStatus;
			} else {
				this.activityId = mActivity.getIntent().getIntExtra(
						"activityId", 0);
				this.interactStatus = mActivity.getIntent().getIntExtra(
						"interactiveStatus", 0);
				this.delayOver = mActivity.getIntent().getLongExtra(
						"delayOver", 0);
				startOverHandler(0);
			}
			interactLayout = (RelativeLayout) mActivity
					.findViewById(R.id.interact_layout);
			interactLayout.setOnClickListener(this);
			interactImg = (ImageView) mActivity.findViewById(R.id.inter_pic);
			interactTip = (ImageView) mActivity.findViewById(R.id.inter_text);
			interactAnim = AnimationUtils.loadAnimation(mActivity,
					R.anim.interaction_rotate);
		}
	}

	public void showInteractionLayout() {
		if (islive
				&& this.activityId > 0
				&& this.interactStatus == InteractiveActivity.INTERACTION_STATUS_INTIME
				&& interactLayout.getVisibility() != View.VISIBLE) {
			interactLayout.setVisibility(View.VISIBLE);
			interactLayout.startAnimation(AnimationUtils.loadAnimation(
					mActivity, R.anim.push_right_in));
		}
	}

	/**
	 * activity onResume调用：
	 */
	public void onResume() {
		if (islive
				&& activityId > 0
				&& interactStatus == InteractiveActivity.INTERACTION_STATUS_INTIME) {
			startOverHandler(delayOver);
			mActivity.bindService(new Intent(mActivity,
					InteractiveIntimeService.class), intimeConnection,
					Context.BIND_AUTO_CREATE);
			mIntimeResultThread = new IntimeResultThread(this, mActivity);
			mIntimeResultThread.start();
		}
	}

	public void startOverHandler(long delayOver) {
		interHandler.removeMessages(INTERACTIVE_OVER);
		interHandler.removeMessages(INTERACTIVE_OVER_TIME_DESC);
		if (delayOver > 0) {
			this.delayOver = delayOver;
		}
		if (this.delayOver > 0) {
			interHandler.sendEmptyMessageDelayed(INTERACTIVE_OVER,
					this.delayOver);
			interHandler.sendEmptyMessageDelayed(INTERACTIVE_OVER_TIME_DESC,
					5000);
		}
	}

	/**
	 * activity onPause调用：
	 */
	public void onPause() {
		if (islive
				&& activityId > 0
				&& interactStatus == InteractiveActivity.INTERACTION_STATUS_INTIME) {
			interHandler.removeMessages(INTERACTIVE_OVER);
			interHandler.removeMessages(INTERACTIVE_OVER_TIME_DESC);
			if (mInteractiveIntimeService != null) {
				mInteractiveIntimeService.stopThread();
				mInteractiveIntimeService.setListener(null);
				mActivity.unbindService(intimeConnection);
				mInteractiveIntimeService = null;
			}

			if (mIntimeResultThread != null) {
				mIntimeResultThread.stop(true);
				mIntimeResultThread.setListener(null);
				mIntimeResultThread = null;
			}
		}
	}

	private InteractiveIntimeService mInteractiveIntimeService;

	private IntimeResultThread mIntimeResultThread;

	private ServiceConnection intimeConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mInteractiveIntimeService = ((InteractiveBinder) service)
					.getService();
			mInteractiveIntimeService.setListener(InteractiveUtils.this);
			mInteractiveIntimeService.inTimeGuessTask();
		}
	};

	// 实时
	public static InteractiveGuess sIntimeGuess = new InteractiveGuess();

	private int interactionDuration;

	InteractionFragment mInteractionFragment;

	private void showInteractionDialog(boolean showChoiceResult) {
		mInteractionFragment = InteractionFragment
				.newInstance(
						mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
						interactionDuration, false, sIntimeGuess,
						showChoiceResult);
		mInteractionFragment.setIntimeJoinListener(this);
		mInteractionFragment.show(mActivity.getSupportFragmentManager(),
				"interaction");
	}

	private boolean startInteractive;

	private void interactionStart() {
		startInteractive = true;
		interactImg.setImageBitmap(WebpUtils.getAssetBitmap(mActivity,
				"webp/interaction_pic.webp"));
		interactTip.setImageResource(R.drawable.interaction_text);
		interactImg.startAnimation(interactAnim);
		interHandler.sendEmptyMessageDelayed(HANDLER_DUR, 1000);
		playVoice();
		Vibrator vib = (Vibrator) mActivity
				.getSystemService(Context.VIBRATOR_SERVICE);
		vib.vibrate(new long[] { 500, 200, 500, 200 }, -1);

	}

	private void playVoice() {
		Intent intent = new Intent(mActivity, PlayAudioService.class);
		intent.putExtra("raw", R.raw.guess_tip);
		mActivity.startService(intent);
	}

	private static final int HANDLER_DUR = 1;

	private static final int RESULT_CLOSE = 2;

	private static final int INTERACTIVE_OVER = 3;

	private static final int INTERACTIVE_OVER_TIME_DESC = 4;

	private Handler interHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_DUR:
				interactionDuration--;
				if (interactionDuration <= 0) {
					interHandler.removeMessages(HANDLER_DUR);
					interactAnim.cancel();
					interactImg.setImageBitmap(WebpUtils.getAssetBitmap(
							mActivity, "webp/interaction_pic_wait.webp"));
					interactTip
							.setImageResource(R.drawable.interaction_text_wait);
				}
				interHandler.sendEmptyMessageDelayed(HANDLER_DUR, 1000);
				break;
			case RESULT_CLOSE:
				closeDialog();
				break;
			case INTERACTIVE_OVER:
				interactLayout.setVisibility(View.GONE);
				onPause();
				break;
			case INTERACTIVE_OVER_TIME_DESC:
				if (delayOver > 0) {
					delayOver -= 5000;
					interHandler.sendEmptyMessageDelayed(
							INTERACTIVE_OVER_TIME_DESC, 5000);
				} else {
					interHandler.removeMessages(INTERACTIVE_OVER_TIME_DESC);
				}
				break;
			default:
				break;
			}

		};
	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.interact_layout) {
			if (startInteractive) {
				if (UserNow.current().totalPoint >= sIntimeGuess.prizeCount) {
					if (sIntimeGuess.userOption != null
							|| selectionOption != null) {
						showInteractionDialog(true);
					} else if (interactionDuration > 5) {
						showInteractionDialog(false);
					} else {
						Toast.makeText(mActivity,
								R.string.waiting_next_interactive,
								Toast.LENGTH_SHORT).show();
						mActivity.startActivity(new Intent(mActivity,
								InteractiveHelpActivity.class));
					}
				} else {
					Toast.makeText(mActivity, R.string.no_enough_point_join,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				if (mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
					mActivity.startActivity(new Intent(mActivity,
							InteractiveHelpActivity.class));
				}
			}
		}

	}

	@Override
	public void startInteract(InteractiveGuess intimeGuess) {
		sIntimeGuess.userOption = null;
		if (activityId == intimeGuess.activityId) {
			sIntimeGuess = intimeGuess;
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date startTime = null;
			try {
				startTime = format.parse(intimeGuess.startTime);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			long start = startTime.getTime() / 1000;
			long now = new Date().getTime() / 1000;
			if (start >= now) {
				interactionDuration = intimeGuess.interactiveDuration;
			} else if ((start + intimeGuess.interactiveDuration) >= now) {
				interactionDuration = (int) (start
						+ intimeGuess.interactiveDuration - now);
			}
			if (interactionDuration > 0) {
				if (interactionDuration > 3600) {
					interactionDuration = 60;
				}
				interactionStart();
			}
		}

	}

	@Override
	public void interactResult(InteractiveGuess intimeGuess) {
		if (startInteractive) {
			startInteractive = false;
			if (!interactAnim.hasEnded()) {
				interHandler.removeMessages(HANDLER_DUR);
				interactAnim.cancel();
				interactionDuration = 0;
				interactImg.setImageBitmap(WebpUtils.getAssetBitmap(mActivity,
						"webp/interaction_pic_wait.webp"));
				interactTip.setImageResource(R.drawable.interaction_text_wait);
			}
			closeDialog();
			if (sIntimeGuess.userOption != null) {
				intimeGuess.userOption = sIntimeGuess.userOption;
				sIntimeGuess = intimeGuess;
				showInteractionDialog(false);
				interHandler.sendEmptyMessageDelayed(RESULT_CLOSE, 3000);
			}
		}
	}

	GuessOption selectionOption;

	private void closeDialog() {
		if (mInteractionFragment != null && mInteractionFragment.isVisible()) {
			mInteractionFragment.dismiss();
			mInteractionFragment = null;
		}

	}

	@Override
	public void inTimeJoin(GuessOption selectionOption) {
		this.selectionOption = selectionOption;
		VolleyInteractionRequest.joinGuess(this, this, sIntimeGuess.id,
				selectionOption.id, sIntimeGuess.prizeCount);
	}

	@Override
	public void JoinGuessResult(int errCode, GuessResult guessResult) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			// this.guessResult = guessResult;
			sIntimeGuess.userOption = selectionOption;
		} else {
			closeDialog();
			interHandler.removeMessages(HANDLER_DUR);
			interactAnim.cancel();
			interactionDuration = 0;
			interactImg.setImageBitmap(WebpUtils.getAssetBitmap(mActivity,
					"webp/interaction_pic_wait.webp"));
			interactTip.setImageResource(R.drawable.interaction_text_wait);
			Toast.makeText(mActivity, R.string.intime_join_failed,
					Toast.LENGTH_LONG).show();
		}
		selectionOption = null;
	}

	@Override
	public void onError(int code) {
		// TODO Auto-generated method stub

	}

}
