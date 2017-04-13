package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.ColumnData;

public interface OnSpecicalListener {

	public void getSpecialColumns(int errCode,int columnCount,ArrayList<ColumnData>columnList);
}
