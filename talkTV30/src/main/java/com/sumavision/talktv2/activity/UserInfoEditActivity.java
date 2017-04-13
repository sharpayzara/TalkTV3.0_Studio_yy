package com.sumavision.talktv2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.andexert.library.RippleView.OnAnimationEndListener;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.LocalInfo;
import com.sumavision.talktv2.application.TalkTvApplication;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.UserDetailParser;
import com.sumavision.talktv2.http.json.UserDetailRequest;
import com.sumavision.talktv2.http.listener.OnUserUpdateListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.share.AccessTokenKeeper;
import com.sumavision.talktv2.share.AuthManager;
import com.sumavision.talktv2.share.sina.SinaAuthManager;
import com.sumavision.talktv2.utils.Base64;
import com.sumavision.talktv2.utils.BitmapUtils;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.FileInfoUtils;
import com.sumavision.talktv2.utils.MediaInfoUtils;
import com.sumavision.talktv2.utils.PicUtils;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * 
 * @description
 * @createTime 2012-6-14
 * @changeLog 2013-1-7
 */
public class UserInfoEditActivity extends BaseActivity implements
		OnClickListener, OnUserUpdateListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.userinfo_person_title);
		setContentView(R.layout.activity_userinfo_edit);
		mPicUtils = new PicUtils(this);
		initView();
		setListeners();
		setOriginalValue();
		if (UserNow.current().showAlert){
			ToastHelper.showToast(this,"您的初始密码为123456");
		}
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("UserInfoEditActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("UserInfoEditActivity");
		super.onResume();
	}

	private TextView nameText;
	private ImageView iconImageView;

	private ImageView maleImageView;
	private ImageView femaleImageView;
	private boolean isMale;

	private void initView() {
		nameText = (TextView) findViewById(R.id.name);
		iconImageView = (ImageView) findViewById(R.id.head_pic);
		maleImageView = (ImageView) findViewById(R.id.ue_male);
		femaleImageView = (ImageView) findViewById(R.id.ue_female);
		initLoadingLayout();
		updateUserInfo();
	}

	private void setOriginalValue() {
		String name = UserNow.current().name;
		if (name != null) {
			nameText.setText(name);
		}
		if (UserNow.current().gender == 1) {
			maleImageView.setSelected(true);
			isMale = true;
		} else {
			femaleImageView.setSelected(true);
			isMale = false;
		}
		String url = UserNow.current().iconURL;
		if (url != null) {
			loadImage(iconImageView, url, R.drawable.list_headpic_default);
		}
	}

	private void setListeners() {
		maleImageView.setOnClickListener(this);
		femaleImageView.setOnClickListener(this);
		iconImageView.setOnClickListener(this);
		findViewById(R.id.userinfo_email_btn).setOnClickListener(this);
		findViewById(R.id.userinfo_psd_btn).setOnClickListener(this);
		findViewById(R.id.rlayout_bind).setOnClickListener(this);
		maleImageView.setOnClickListener(this);
		// findViewById(R.id.zuxiao).setOnClickListener(this);
		RippleView logoutRipple = (RippleView) findViewById(R.id.rect);
		logoutRipple.setOnAnimationEndListener(new OnAnimationEndListener() {

			@Override
			public void OnAnimationEnd() {
				onClick(findViewById(R.id.zuxiao));
			}
		});
	}
	public void updateUserInfo(){
		VolleyHelper.post(new UserDetailRequest().make(), new ParseListener(new UserDetailParser()) {
			@Override
			public void onParse(BaseJsonParser parser) {
				EventBus.getDefault().post(new UserInfoEvent());
			}
		}, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ue_male:
			if (!isMale) {
				maleImageView.setSelected(true);
				femaleImageView.setSelected(false);
				isMale = true;
				updateGender();
			}
			break;
		case R.id.ue_female:
			if (isMale) {
				maleImageView.setSelected(false);
				femaleImageView.setSelected(true);
				isMale = false;
				updateGender();
			}
			break;
		case R.id.head_pic:
			CharSequence[] items = { "相册", "拍照" };
			ChooseImage(items);
			break;
		case R.id.zuxiao:
			MobclickAgent.onEvent(this, "zhuxiao");
			PreferencesUtils.clearAll(this, "userInfo");
			AccessTokenKeeper.clear(this);
			AccessTokenKeeper.clearQQInfo(this);
			AuthManager.getInstance(this).logOut(SHARE_MEDIA.QQ);
			new SinaAuthManager().logout(this);
			setPushValue();
			UserNow.current().level = "";
			UserNow.current().exp = 0;
			UserNow.current().point = 0;
			UserNow.current().totalPoint = 0;
			UserNow.current().diamond = 0;
			UserNow.current().userID = 0;
			UserNow.current().isLogedIn = false;
			UserNow.current().isSelf = false;
			UserNow.current().signature = "";
			UserNow.current().iconURL = "";
			UserNow.current().name = "";
			UserNow.current().eMail = "";
			UserNow.current().showAlert = false;
			UserNow.current().isVip = false;
			UserNow.current().dayLoterry = 0;
					((TalkTvApplication) getApplication()).mAddressData = null;
			logOff();
			EventBus.getDefault().post(new UserInfoEvent());
			finish();
			break;
		case R.id.userinfo_email_btn:
			Intent intent = new Intent();
			intent.setClass(this, ChangeEmailActivity.class);
			startActivity(intent);
			break;
		case R.id.userinfo_psd_btn:
			intent = new Intent();
			intent.setClass(this, ChangePasswdActivity.class);
			startActivity(intent);
			break;
		case R.id.rlayout_bind:
			startActivity(new Intent(this, PlatformBindActivity.class));
			break;
		default:
			break;
		}
	}

	private void setPushValue() {
		PreferencesUtils.putBoolean(this, Constants.pushMessage,
				Constants.key_privateMsg, false);
		PreferencesUtils.putBoolean(this, Constants.pushMessage,
				Constants.key_fans, false);
		PreferencesUtils.putBoolean(this, Constants.pushMessage,
				Constants.key_reply, false);
		PreferencesUtils.putBoolean(this, Constants.pushMessage,
				Constants.key_favourite, false);
		PreferencesUtils.putBoolean(this, Constants.pushMessage,
				Constants.key_user_comment, false);
		PreferencesUtils.putBoolean(this, Constants.pushMessage,
				Constants.KEY_USER_CENTER, false);
	}

	UserModify mUserModify = new UserModify();

	private void updateGender() {
		if (isMale) {
			mUserModify.gender = 1;
		} else {
			mUserModify.gender = 2;
		}
		mUserModify.genderFlag = 1;
		if (lastChangePic) {
			mUserModify.picFlag = 1;
		}
		sendUserUpdateInfo();
	}

	private void logOff() {
		VolleyUserRequest.logOff(null, null);
	}

	private void sendUserUpdateInfo() {
		showLoadingLayout();
		VolleyUserRequest.UserUpdate(this, this, mUserModify);
	}

	private boolean lastChangePic;

	private Bitmap result;
	private PicUtils mPicUtils;

	private static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 1;
	private static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 2;
	public static final int PHOTORESOULT = 4;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private String theLarge = null;

	public void ChooseImage(CharSequence[] items) {
		AlertDialog imageDialog = new AlertDialog.Builder(
				UserInfoEditActivity.this).setTitle("选择图片")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {

						if (item == 0) {
							Intent intent = new Intent(
									Intent.ACTION_PICK);
							intent.setType("image/*");
							startActivityForResult(intent,
									REQUEST_CODE_GETIMAGE_BYSDCARD);
						} else if (item == 1) {
							Intent intent = new Intent(
									"android.media.action.IMAGE_CAPTURE");

							String camerName = PicUtils.getFileName();
							String fileName = "TalkTV" + camerName + ".tmp";

							File fileDir = new File(
									JSONMessageType.USER_PIC_SDCARD_FOLDER);
							if (!fileDir.exists()) {
								fileDir.mkdir();
							}

							File camerFile = new File(
									JSONMessageType.USER_PIC_SDCARD_FOLDER,
									fileName);
							UserNow.current().picPath = camerFile
									.getAbsolutePath();

							Uri originalUri = Uri.fromFile(camerFile);
							intent.putExtra(MediaStore.EXTRA_OUTPUT,
									originalUri);
							startActivityForResult(intent,
									REQUEST_CODE_GETIMAGE_BYCAMERA);
						}
					}
				}).create();

		imageDialog.show();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PHOTORESOULT:
				Bundle extras = data.getExtras();
				int degree = readPictureDegree(UserNow.current().picPath);
				if (extras != null) {
					result = extras.getParcelable("data");
					if (degree != 0) {
						result = BitmapUtils.rotateBitmap(result, degree);
					}
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					result.compress(Bitmap.CompressFormat.JPEG, 100, stream);

					String str = new String(Base64.encode(
							BitmapUtils.bitmapToBytes(result), 0,
							BitmapUtils.bitmapToBytes(result).length));
					mUserModify.pic_Base64 = str;
					iconImageView.setImageDrawable(new BitmapDrawable(result));
					mUserModify.picFlag = 1;
					sendUserUpdateInfo();
					lastChangePic = true;
				}
				break;
			case REQUEST_CODE_GETIMAGE_BYCAMERA:
				Uri uri = Uri.fromFile(new File(UserNow.current().picPath));
				startPhotoZoom(uri);
				break;
			case REQUEST_CODE_GETIMAGE_BYSDCARD:
				if (data == null)
					return;
				Uri thisUri = data.getData();
				String thePath = PicUtils
						.getAbsolutePathFromNoStandardUri(thisUri);

				if (com.sumavision.talktv2.utils.StringUtils.isBlank(thePath)) {
					theLarge = mPicUtils.getAbsoluteImagePath(thisUri);
				} else {
					theLarge = thePath;
				}

				String attFormat = FileInfoUtils.getFileFormat(theLarge);
				if (!"photo".equals(MediaInfoUtils.getContentType(attFormat))) {
					Toast.makeText(UserInfoEditActivity.this, "请选择图片文件！",
							Toast.LENGTH_SHORT).show();
					return;
				}

				Uri uri1 = Uri.fromFile(new File(theLarge));
				startPhotoZoom(uri1);
				break;
			default:
				break;
			}
		}
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTORESOULT);
	}

	@Override
	public void updateUserResult(int errCode, String errMsg) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			EventBus.getDefault().post(new UserInfoEvent());
			DialogUtil.alertToast(getApplicationContext(), "修改成功");
		} else {
			DialogUtil.alertToast(getApplicationContext(), "修改失败");
		}
		if (mUserModify.passwdNewFlag == 1) {
			UserNow.current().passwd = mUserModify.passwdNew;
		}
		LocalInfo.SaveUserData(this, true);
	}

}
