package com.sumavision.talktv2.activity.help;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.view.ActionProvider;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.DynamicActivity;

/**
 * 用户空间菜单
 * 
 * @author suma-hpb
 * 
 */
public class UserCenterActionProvider extends ActionProvider {
	Context mContext;

	public UserCenterActionProvider(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public View onCreateActionView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.action_user_center, null);
		view.findViewById(R.id.tv_dhall).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mContext.startActivity(new Intent(mContext,
								DynamicActivity.class));
					}
				});
		return view;
	}

	@Override
	public boolean onPerformDefaultAction() {
		return super.onPerformDefaultAction();
	}

}
