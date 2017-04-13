package com.sumavision.talktv2.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.CustomTextWatcher;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.adapter.EmotionImageAdapter;
import com.sumavision.talktv2.adapter.PhrasesListAdapter;
import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.DialogInfo;
import com.sumavision.talktv2.bean.EmotionData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.MakeEmotionsList;
import com.sumavision.talktv2.bean.SinaData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.fragment.ChoosePicDialogFragment;
import com.sumavision.talktv2.fragment.CommonDialogFragment;
import com.sumavision.talktv2.fragment.CommonDialogFragment.OnCommonDialogListener;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.ShareRequest;
import com.sumavision.talktv2.http.listener.OnSendCommentListener;
import com.sumavision.talktv2.http.listener.OnSendFowardListener;
import com.sumavision.talktv2.http.listener.OnSendReplyListener;
import com.sumavision.talktv2.http.request.VolleyCommentRequest;
import com.sumavision.talktv2.share.sina.SinaAuthManager;
import com.sumavision.talktv2.share.sina.SinaAuthManager.OnSinaBindLoginListener;
import com.sumavision.talktv2.share.sina.SinaShareManager;
import com.sumavision.talktv2.utils.BitmapUtils;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.FileInfoUtils;
import com.sumavision.talktv2.utils.MediaInfoUtils;
import com.sumavision.talktv2.utils.PicUtils;
import com.sumavision.talktv2.utils.Txt2Image;
import com.umeng.analytics.MobclickAgent;

/**
 * @author hpb
 * @version 3.0
 * @description 发表评论
 * @changeLog
 */
public class SendCommentActivity extends BaseActivity implements
		OnClickListener, OnSendCommentListener, OnSendFowardListener,
		OnSendReplyListener {
	private final int LOGIN = 13;
	public static final int NORMAL = 0;
	public static final int REPLY = 1;
	public static final int FORWARD = 2;
	// public static final int SCREENSHOT = 3;
	public static final int SHARE2SINA = 4;
	private EditText input;
	private ImageView choosedPic;
	private ImageButton sync;
	private LinearLayout syncLayout;
	private RelativeLayout browLayout, phraseLayout, picFrame, loginLayout;
	private List<EmotionData> ids;
	private EmotionImageAdapter eia;
	private PhrasesListAdapter pla;
	private GridView emotionGrid;
	private ListView phraseList;
	private Animation a;
	private String thisLarge = null;
	private PicUtils mPicUtils;
	private Bitmap result;
	private boolean hasEmotions = false;
	private boolean hasPhraseList = false;
	private String programName;
	private String topicId;
	private int fromWhere = NORMAL;
	private int subid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.navigator_send_comment);
		setContentView(R.layout.activity_send_comment);
		fromWhere = getIntent().getIntExtra("fromWhere", NORMAL);
		getIntentData();
		msinaAuthManager = new SinaAuthManager();
		mSinaShareManager = new SinaShareManager(this, null);
		mSinaShareManager.init(savedInstanceState);
		initViews();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("SendCommentActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("SendCommentActivity");
		super.onResume();
	}

	SinaAuthManager msinaAuthManager;
	SinaShareManager mSinaShareManager;

	String programId;

	private void getIntentData() {
		Intent i = getIntent();
		programId = i.getStringExtra("programId");
		subid = i.getIntExtra("subid", 0);
		if (i.hasExtra("programName"))
			programName = i.getStringExtra("programName");
		if (i.hasExtra("topicId"))
			topicId = i.getStringExtra("topicId");
		if (i.hasExtra("sinaPic")) {// sina微博自带图片
			sinaPic = i.getStringExtra("sinaPic");
		}
	}

	private ArrayList<String> phrList = new ArrayList<String>();
	private TextView lengthTextvView;

	private void initViews() {
		String[] arr = getResources().getStringArray(R.array.comment_phr);
		for (String s : arr) {
			phrList.add(s);
		}
		initLoadingLayout();
		lengthTextvView = (TextView) findViewById(R.id.tv_left_count);
		findViewById(R.id.emotion).setOnClickListener(this);
		findViewById(R.id.photo).setOnClickListener(this);
		findViewById(R.id.duanyu).setOnClickListener(this);
		syncLayout = (LinearLayout) findViewById(R.id.llayout_sync);
		sync = (ImageButton) findViewById(R.id.sync);
		input = (EditText) findViewById(R.id.content_text);
		input.setOnClickListener(this);
		input.addTextChangedListener(new CustomTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				lengthTextvView.setText(String.valueOf(140 - input.getText()
						.length()));
			}
		});
		picFrame = (RelativeLayout) findViewById(R.id.sendcomment_layout_pic);
		picFrame.setOnClickListener(this);
		choosedPic = (ImageView) findViewById(R.id.sendcomment_choosed_pic);
		choosedPic.setOnClickListener(this);
		picFrame.findViewById(R.id.sendcomment_chacha_pic).setOnClickListener(
				this);
		picFrame.setOnClickListener(this);

		phraseLayout = (RelativeLayout) findViewById(R.id.pdn_c_relative_phrase);
		browLayout = (RelativeLayout) findViewById(R.id.pdn_c_relative_emotion);
		emotionGrid = (GridView) findViewById(R.id.pdn_c_grid_emotion);
		phraseList = (ListView) findViewById(R.id.pdn_c_list_phrase);
		a = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		ids = MakeEmotionsList.current().getLe();
		eia = new EmotionImageAdapter(this, ids);
		emotionGrid.setAdapter(eia);
		emotionGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int cursor = input.getSelectionStart();
				EmotionData e = ids.get(arg2);
				input.getText().insert(
						cursor,
						Txt2Image.txtToImg(e.getPhrase(),
								SendCommentActivity.this));
			}
		});
		pla = new PhrasesListAdapter(this, phrList);
		phraseList.setAdapter(pla);
		phraseList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int cursor = input.getSelectionStart();
				input.getText().insert(cursor, phrList.get(arg2));
			}
		});

		mPicUtils = new PicUtils(this);

		switch (fromWhere) {
		case NORMAL:
			syncLayout.setVisibility(View.VISIBLE);
			sync.setOnClickListener(this);
			CommentData.current().pic = "";
			picSet(false);
			break;
		case FORWARD:
			syncLayout.setVisibility(View.GONE);
			if (CommentData.current().hasRootTalk)
				input.setText("//@" + CommentData.current().userName + ":"
						+ CommentData.current().content);
			input.setSelection(0);
			CommentData.current().pic = "";
			picSet(false);
			break;
		case REPLY:
			syncLayout.setVisibility(View.GONE);
			CommentData.current().pic = "";
			picSet(false);
			// input.setText("回复 " + CommentData.current().userName + ":");
			break;
		case SHARE2SINA:
			if (sinaPic != null) {
				CommentData.current().picAllName = sinaPic;
			} else {
				CommentData.current().picAllName = null;
			}
			picSet(false);
			if (shareToSina) {
				sync.setImageResource(R.drawable.sina);
				shareToSina = false;
			} else {
				msinaAuthManager.auth(this, new OnSinaBindLoginListener() {

					@Override
					public void OnSinaBindSucceed() {
						shareToSina = true;
						sync.setImageResource(R.drawable.sina_selected_normal);
					}
				});
			}
			input.setText("真是太精彩了 !");
			break;
		default:
			break;
		}
		loginLayout = (RelativeLayout) findViewById(R.id.pdn_c_login_layout);
		findViewById(R.id.pdn_c_login_btn).setOnClickListener(this);
		findViewById(R.id.pdn_c_send_btn).setOnClickListener(this);

	}

	TextActionProvider sendAction;

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.send_comment, menu);
		sendAction = (TextActionProvider) menu.findItem(
				R.id.action_send_comment).getActionProvider();
		if (sendAction == null){
			return super.onCreateOptionsMenu(menu);
		}
		sendAction.setShowText(R.string.navigator_btn_send);
		sendAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendCommentAction();
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	private void sendCommentAction() {
		String content = input.getText().toString().trim();
		if (content.length() == 0) {
			Toast.makeText(this, "请先说点什么...", Toast.LENGTH_SHORT).show();
			input.startAnimation(a);
		} else {
			SinaData.content = "#我在电视粉评论#" + "《" + programName + "》：" + content
					+ "(来自@电视粉)" + "\n";
			hasEmotions = false;
			browLayout.setVisibility(View.GONE);
			hideSoftPad();
			phraseLayout.setVisibility(View.GONE);
			hasPhraseList = false;
			CommentData.current().content = content;
			int nowSize = content.length();
			if (shareToSina) {
				if (nowSize > 50) {
					nowSize -= 50;
					Toast.makeText(getApplicationContext(),
							"请删除" + nowSize + "个字后重试", Toast.LENGTH_SHORT)
							.show();
				} else {
					StringBuilder share2sinaTxt = new StringBuilder("快来看");
					if (!programName.isEmpty()) {
						share2sinaTxt.append("#").append(programName)
								.append("#");
					}
					StringBuilder builder = new StringBuilder(Constants.url + "web/mobile/shareProgram.action?subId=" + subid);
					share2sinaTxt.append("，").append(content)
							.append("，观看地址>>>").append(builder.toString())
							.append(" (来自@电视粉)" + "\n");
					SinaData.content = share2sinaTxt.toString();
				}
				sendInfo();
			} else if (UserNow.current().userID != 0) {
				sendInfo();
			} else {
				loginLayout.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.content_text:
			hasEmotions = false;
			browLayout.setVisibility(View.GONE);
			phraseLayout.setVisibility(View.GONE);
			hasPhraseList = false;
			break;
		case R.id.emotion:
			MobclickAgent.onEvent(this, "biaoqing");
			hideSoftPad();
			phraseLayout.setVisibility(View.GONE);
			hasPhraseList = false;
			if (!hasEmotions) {
				browLayout.setVisibility(View.VISIBLE);
				hasEmotions = true;
			} else {
				hasEmotions = false;
				browLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.photo:
			MobclickAgent.onEvent(this, "pic");
			CharSequence[] items = { "相册", "拍照" };
			ChooseImage(items);
			break;
		case R.id.duanyu:
			MobclickAgent.onEvent(this, "changyong");
			hideSoftPad();
			hasEmotions = false;
			browLayout.setVisibility(View.GONE);
			if (!hasPhraseList) {
				phraseLayout.setVisibility(View.VISIBLE);
				hasPhraseList = true;
			} else {
				hasPhraseList = false;
				phraseLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.sync:
			// MobclickAgent.onEvent(this, "sinaweibo");

			if (shareToSina) {
				sync.setImageResource(R.drawable.sina);
				shareToSina = false;
			} else {
				msinaAuthManager.auth(this, new OnSinaBindLoginListener() {

					@Override
					public void OnSinaBindSucceed() {
						shareToSina = true;
						sync.setImageResource(R.drawable.sina_selected_normal);
					}
				});
			}
			break;
		case R.id.sendcomment_choosed_pic:
		case R.id.sendcomment_layout_pic:
			cleanTextOrPic(false);
			break;
		case R.id.pdn_c_login_btn:
			loginLayout.setVisibility(View.GONE);
			Intent i = new Intent(this, LoginActivity.class);
			startActivityForResult(i, LOGIN);
			break;
		case R.id.pdn_c_send_btn:
			MobclickAgent.onEvent(this, "fabiao");
			loginLayout.setVisibility(View.GONE);
			sendInfo();
			break;
		default:
			break;
		}
	}

	boolean shareToSina;

	private void sendForward() {
		showLoadingLayout();
		VolleyCommentRequest.sendForward(this, this);
	}

	private void sendReply() {
		showLoadingLayout();
		VolleyCommentRequest.sendReply(this, this);
	}

	private void sendComment() {
		showLoadingLayout();
		if (CommentData.current().audio == null) {
			if (CommentData.current().content.equals("")) {
				Toast.makeText(getApplicationContext(), "请先说点什么...",
						Toast.LENGTH_SHORT).show();
			} else {
				VolleyCommentRequest.sendComment(this, topicId, this);
				if (shareToSina) {
					precessWeibo();
				}
			}
		} else {
			VolleyCommentRequest.sendComment(this, topicId, this);
		}
	}

	private void precessWeibo() {
		if (sinaPic != null) {
			CommentData.current().picAllName = sinaPic;
			SinaData.pic = sinaPic;
		}

		Bitmap bitmap = null;
		if (choosedPic.getDrawable() != null) {
			bitmap = ((BitmapDrawable) choosedPic.getDrawable()).getBitmap();
		}
		mSinaShareManager.share(SinaData.content, bitmap);
	}

	public void ChooseImage(CharSequence[] items) {
		ChoosePicDialogFragment dialog = ChoosePicDialogFragment
				.newInstance(items);
		dialog.show(getSupportFragmentManager(), "img");
	}

	private void cleanTextOrPic(final boolean cleanText) {
		DialogInfo info = new DialogInfo();
		info.title = getString(R.string.my_function_comment);
		info.confirm = "确定";
		info.content = cleanText ? "清空所有已输入的内容么?" : "清空图片?";
		info.cancel = "取消";
		CommonDialogFragment dialog = CommonDialogFragment.newInstance(info,
				true);
		dialog.setOnClickListener(new OnCommonDialogListener() {

			@Override
			public void onPositiveButtonClick() {
				if (cleanText) {
					input.setText("");
				} else {
					picSet(false);
				}
			}

			@Override
			public void onNeutralButtonClick() {

			}

			@Override
			public void onNegativeButtonClick() {

			}
		});
		dialog.show(getSupportFragmentManager(), "dialog");
	}

	@SuppressWarnings("deprecation")
	private void picSet(boolean hasPic) {
		if (!hasPic) {
			choosedPic.setVisibility(View.GONE);
			picFrame.setVisibility(View.GONE);
			CommentData.current().pic = "";
			CommentData.current().picBitMap = null;
			CommentData.current().picAllName = null;
		} else {
			choosedPic.setVisibility(View.VISIBLE);
			choosedPic.setAlpha(130);
			picFrame.setVisibility(View.VISIBLE);
		}
	}

	private void hideSoftPad() {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(SendCommentActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		msinaAuthManager.onActivityResult(requestCode, resultCode, data);
		// UMSsoHandler ssoHandler =
		// ShareManager.getInstance(this).getSsoHandler(
		// requestCode);
		// if (ssoHandler != null) {
		// ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		// }
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case LOGIN:
				sendInfo();
				break;
			case ChoosePicDialogFragment.GETIMAGE_BYCAMERA:
				picSet(true);
				Bitmap bitmap = PicUtils.getScaleBitmap(
						SendCommentActivity.this, UserNow.current().picPath);
				result = PicUtils.getFinalScaleBitmapBigPic(this,
						UserNow.current().picPath);

				String s1 = new String(
						com.sumavision.talktv2.utils.Base64.encode(
								BitmapUtils.bitmapToBytes(result), 0,
								BitmapUtils.bitmapToBytes(result).length));
				CommentData.current().pic = s1;
				CommentData.current().picBitMap = result;
				CommentData.current().picAllName = UserNow.current().picPath;
				// CommentData.current().setPicLogo(
				// BitmapUtils.bitmapToBytes(result));
				if (bitmap != null) {
					choosedPic.setImageDrawable(new BitmapDrawable(bitmap));
				}
				
				break;
			case ChoosePicDialogFragment.GETIMAGE_BYSDCARD:
				if (data == null)
					return;
				picSet(true);
				Uri thisUri = data.getData();
				String thePath = PicUtils
						.getAbsolutePathFromNoStandardUri(thisUri);
				// 如果是标准Uri
				if (com.sumavision.talktv2.utils.StringUtils.isBlank(thePath)) {
					thisLarge = mPicUtils.getAbsoluteImagePath(thisUri);
				} else {
					thisLarge = thePath;
				}

				String attFormat = FileInfoUtils.getFileFormat(thisLarge);
				if (!"photo".equals(MediaInfoUtils.getContentType(attFormat))) {
					Toast.makeText(SendCommentActivity.this, "请选择图片文件！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String imgName = FileInfoUtils.getFileName(thisLarge);
				result = PicUtils.getFinalScaleBitmapBigPic(this, thisLarge);

				String s = new String(
						com.sumavision.talktv2.utils.Base64.encode(
								BitmapUtils.bitmapToBytes(result), 0,
								BitmapUtils.bitmapToBytes(result).length));

				CommentData.current().pic = s;
				CommentData.current().picBitMap = result;
				CommentData.current().picAllName = thisLarge;

				// CommentData.current().setPicLogo(
				// BitmapUtils.bitmapToBytes(result));

				Bitmap b = mPicUtils.loadImgThumbnail(imgName,
						MediaStore.Images.Thumbnails.MICRO_KIND);
				if (b != null) {
					choosedPic.setImageDrawable(new BitmapDrawable(b));
				}
				input.append(thisUri.toString()+"\n");
				if(!TextUtils.isEmpty(thePath)){
					input.append(thePath.toString()+"\n");
				}
				input.append(thisLarge.toString()+"\n");
				
				break;
			default:
				break;
			}
		}
	}

	private void sendInfo() {
		switch (fromWhere) {
		case NORMAL:
		case SHARE2SINA:
			sendComment();
			break;
		case REPLY:
			sendReply();
			break;
		case FORWARD:
			sendForward();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (browLayout.isShown()) {
				hasEmotions = false;
				browLayout.setVisibility(View.GONE);
				return true;
			} else if (phraseLayout.isShown()) {
				phraseLayout.setVisibility(View.GONE);
				hasPhraseList = false;
				return true;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	// 视频播放页面带来的节目剧照
	private String sinaPic;

	@Override
	public void sendReplyResult(int errCode) {
		sendResult(errCode);
	}

	@Override
	public void sendForwardResult(int errCode) {
		sendResult(errCode);
	}

	@Override
	public void sendCommentResult(int errCode) {
		sendResult(errCode);
	}

	ResultParser shareParser = new ResultParser();

	private void shareRequest(int programId) {
		VolleyHelper.post(
				new ShareRequest(ShareRequest.TYPE_PROGRAM, programId).make(),
				new ParseListener(shareParser) {

					@Override
					public void onParse(BaseJsonParser parser) {
//						if (shareParser.errCode == JSONMessageType.SERVER_CODE_OK) {
//							if (parser.userInfo.point > 0) {
//								UserNow.current().setTotalPoint(
//										parser.userInfo.totalPoint,parser.userInfo.vipIncPoint);
//							}
//						}
					}
				}, null);
	}

	private void sendResult(int errCode) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			if (!TextUtils.isEmpty(programId)) {
				if (fromWhere == SHARE2SINA || shareToSina) {
					if (UserNow.current().userID > 0) {
						shareRequest(Integer.parseInt(programId));
					}
				}
			}
			Toast.makeText(this, "发表成功", Toast.LENGTH_SHORT).show();
			CommentData.current().content = "";
			CommentData.current().pic = "";
			CommentData.current().audio = null;
			CommentData.current().picAllName = null;
			setResult(RESULT_OK);
		}
		finish();
	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.talkForwardAdd);
		VolleyHelper.cancelRequest(Constants.replyAdd);
		VolleyHelper.cancelRequest(Constants.talkAdd);
		VolleyHelper.cancelRequest(Constants.shareAnything);
	}

}
