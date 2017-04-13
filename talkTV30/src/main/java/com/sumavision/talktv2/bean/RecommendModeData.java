package com.sumavision.talktv2.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/12.
 */
public class RecommendModeData implements Serializable {
    public int id;
    public int type;
    public String name;
    public long objectId;
    public int objectType;
    public String picWide;
    public ArrayList<VodProgramData> hotLabelPrograms = new ArrayList<VodProgramData>();
    public VodProgramData exchangeToVod(){
        VodProgramData data = new VodProgramData();
        data.id = id+"";
        data.ptype = type;
        data.name = name;
        data.activityId = (int)objectId;
        data.picType = objectType;
        data.pic = picWide;
        data.selected = true;
        return data;
    }
}
