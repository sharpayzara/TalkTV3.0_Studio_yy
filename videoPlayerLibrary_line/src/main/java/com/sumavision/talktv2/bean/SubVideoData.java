package com.sumavision.talktv2.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/5/14.
 */
public class SubVideoData implements Serializable {
    public int programId;
    public int subId;
    public int stage;
    public int platformId;
    public int ptype;
    public String webUrl;
    public String videoPath;
    public String progName;
    public String subName;
    public boolean skipToWeb;
    public boolean isNative = true;

    public int positionFlag;//1.首次加载播放、2.下一集

    public String recVersion;//智能推荐版本
}
