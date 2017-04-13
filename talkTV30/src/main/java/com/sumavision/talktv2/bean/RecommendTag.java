package com.sumavision.talktv2.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/6/8.
 */
public class RecommendTag implements Serializable{
    public long id;
    public String name;
    public String code;
    public int sortOrder;
    public int type;
    public int programTypeId;
    public long columnId;
    public String columnName;
}
