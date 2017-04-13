package com.sumavision.talktv2.fragment;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.PicUtils;

/**
 * 评论添加图片提示
 * 
 * @author suma-hpb
 * 
 */
public class ChoosePicDialogFragment extends DialogFragment {

	public static final int GETIMAGE_BYSDCARD = 15;
	public static final int GETIMAGE_BYCAMERA = 16;
	CharSequence[] items;

	public static ChoosePicDialogFragment newInstance(CharSequence[] items) {
		ChoosePicDialogFragment frag = new ChoosePicDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putCharSequenceArray("items", items);
		frag.setArguments(bundle);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		items = getArguments().getCharSequenceArray("items");
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog imageDialog = new AlertDialog.Builder(getActivity())
				.setTitle("选择图片")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						try {
							if (item == 0) {
								Intent intent = new Intent(
										Intent.ACTION_PICK);
								intent.setType("image/*");
								getActivity().startActivityForResult(intent,
										GETIMAGE_BYSDCARD);
							} else if (item == 1) {
								Intent intent = new Intent(
										"android.media.action.IMAGE_CAPTURE");

								String camerName = PicUtils.getFileName();
								String fileName = "TalkTV" + camerName + ".jpg";

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
								getActivity().startActivityForResult(intent,
										GETIMAGE_BYCAMERA);
							}
						} catch (Exception e) {
							Toast.makeText(getActivity(), "亲，您还没装sd卡哦！",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).create();
		return imageDialog;
	}

}
