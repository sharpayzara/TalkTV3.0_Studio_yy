/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sumavision.talktv2.activity.help;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.actionbarsherlock.view.ActionProvider;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.actionprovider.bean.CustomChooser;
import com.sumavision.talktv2.actionprovider.bean.CustomChooserTextView;
import com.sumavision.talktv2.actionprovider.bean.CustomChooserTextView.OnItemChooserListener;

/**
 * 节目页操作栏：告诉小编
 * 
 * @author suma-hpb
 * 
 */
public class ProgramActionProvider extends ActionProvider {

	private final Context mContext;

	private List<CustomChooser> list;

	private OnItemChooserListener itemListener;

	public void setOnItemChooserListener(OnItemChooserListener itemListener) {
		this.itemListener = itemListener;
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param context
	 *            Context for accessing resources.
	 */
	public ProgramActionProvider(Context context) {
		super(context);
		mContext = context;

		String[] arr = context.getResources().getStringArray(R.array.play_err);
		list = new ArrayList<CustomChooser>();
		for (String title : arr) {
			CustomChooser c = new CustomChooser();
			c.setTitle(title);
			list.add(c);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressLint("ResourceAsColor")
	@Override
	public View onCreateActionView() {
		CustomChooserTextView customChooserView = new CustomChooserTextView(
				mContext);
		customChooserView.setCustomChooserData(list);
		customChooserView.setOnItemChooserListener(itemListener);
		customChooserView.setExpandActivityOverflowText(R.string.tell_me);
		customChooserView.setExpandActivityOverflowTextColor(R.color.white);
		customChooserView.setProvider(this);

		return customChooserView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasSubMenu() {
		return true;
	}

}
