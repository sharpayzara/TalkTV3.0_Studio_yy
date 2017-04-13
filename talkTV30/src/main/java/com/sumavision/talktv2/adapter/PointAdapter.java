package com.sumavision.talktv2.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.RecommandAppActivity;
import com.sumavision.talktv2.bean.PointBase;
import com.sumavision.talktv2.bean.PointList;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 赚积分页适配
 * 
 * @author suma-hpb
 * 
 */
public class PointAdapter extends BaseExpandableListAdapter {

	Activity activity;
	private List<PointList> mList;

	public PointAdapter(Activity a, List<PointList> mList) {
		activity = a;
		this.mList = mList;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mList.get(groupPosition).pointList.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mList.get(groupPosition).pointList.size();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		PointBase pb = mList.get(groupPosition).pointList.get(childPosition);
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.item_point_child, null);
		}

		TextView lefttxt = ViewHolder.get(convertView, R.id.child_left);
		lefttxt.setText(pb.baseName);
		TextView middletxt = ViewHolder.get(convertView, R.id.child_middle);
		middletxt.setText("+" + pb.baseScore + "积分");
		TextView righttxt = ViewHolder.get(convertView, R.id.child_right);
		LinearLayout rightbtn = ViewHolder.get(convertView,
				R.id.child_right_btn);
		int colorFinish = activity.getResources().getColor(R.color.forget_hint);
		int colorMiddle = activity.getResources().getColor(
				R.color.point_child_middle);
		int colorRight = activity.getResources().getColor(
				R.color.point_child_right);
		if (pb.baseFinish && !pb.baseSpecial) {
			rightbtn.setVisibility(View.GONE);
			righttxt.setVisibility(View.VISIBLE);
			righttxt.setText("已完成");
			righttxt.setTextColor(colorFinish);
			middletxt.setTextColor(colorFinish);
		} else if (!pb.baseFinish && !pb.baseSpecial) {
			rightbtn.setVisibility(View.GONE);
			righttxt.setVisibility(View.VISIBLE);
			righttxt.setText("未完成");
			righttxt.setTextColor(colorRight);
			middletxt.setTextColor(colorMiddle);
		} else if (pb.baseSpecial) {
			righttxt.setVisibility(View.GONE);
			rightbtn.setVisibility(View.VISIBLE);
			rightbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					activity.startActivity(new Intent(activity,
							RecommandAppActivity.class));

				}
			});
		}
		return convertView;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		PointList pl = mList.get(groupPosition);
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.item_point_group, null);
		}
		TextView title = ViewHolder.get(convertView, R.id.group_title);
		title.setText(pl.pointTitle);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
