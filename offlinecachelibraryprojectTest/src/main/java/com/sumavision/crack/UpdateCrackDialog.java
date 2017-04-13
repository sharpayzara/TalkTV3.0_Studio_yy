package com.sumavision.crack;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.sumavision.offlinecachelibrary.R;

/**
 * 更新破解jar包和so文件时使用
 * 
 * @author zhangyisu
 *
 */
public class UpdateCrackDialog extends Dialog {

	private Context mctx;

	public UpdateCrackDialog(Context mctx, int theme) {
		super(mctx, theme);
		this.mctx = mctx;
		setContentView(R.layout.update_crack_dialog);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("正在更新播放组件，请稍等……");
	}


}
