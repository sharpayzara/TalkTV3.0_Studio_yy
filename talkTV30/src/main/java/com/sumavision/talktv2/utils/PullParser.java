package com.sumavision.talktv2.utils;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.util.SparseArray;
import android.util.Xml;

import com.sumavision.talktv2.bean.DistrictData;

/**
 * 解析出XML中的省份、城市、地区名
 * */
public class PullParser {
	
	public static ArrayList<DistrictData> pList = new ArrayList<DistrictData>();
	public static SparseArray<ArrayList<DistrictData>> cList = new SparseArray<ArrayList<DistrictData>>();
	public static SparseArray<ArrayList<DistrictData>> dList = new SparseArray<ArrayList<DistrictData>>();
	
	public void parse(InputStream is, int which, String root, String selfId, String name, String parentId) throws Exception {
		ArrayList<DistrictData> cDatas = new ArrayList<DistrictData>();
		DistrictData cData = null;
		int id = -1;
		XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
        	switch (eventType) {
        	case XmlPullParser.START_DOCUMENT:  
                break; 
        	case XmlPullParser.START_TAG: 
        		if (parser.getName().equals(root)) {  
        			cData = new DistrictData();  
        			cData.id = Integer.parseInt(parser.getAttributeValue(null, selfId));
        			cData.name = parser.getAttributeValue(null, name);
        			if (parentId != null) {
        				cData.parentId = Integer.parseInt(parser.getAttributeValue(null, parentId));
        			}
                
        			if (id != cData.parentId && id != -1 && which > 0) {
        				if (which == 1) {
        					cList.put(id, cDatas);
        				} else {
        					dList.put(id, cDatas);
        				}
        				cDatas = new ArrayList<DistrictData>();
        			}
        		}
        		break;
        	case XmlPullParser.END_TAG:
        		if (parser.getName().equals(root)) {
        			id = cData.parentId;
        			cDatas.add(cData);
        			cData = null;
        		}
        		break;
        	}
        	eventType = parser.next();
        }
        if (which <= 0) {
        	pList = cDatas;
        }
	}
}
