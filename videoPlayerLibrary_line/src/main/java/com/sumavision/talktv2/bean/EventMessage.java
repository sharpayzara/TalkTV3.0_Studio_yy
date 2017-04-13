package com.sumavision.talktv2.bean;

import android.os.Bundle;

/**
 * Created by Administrator on 2015/5/4.
 */
public class EventMessage {
    public EventMessage(String str){
        this.name = str;
    }
    public String name;
    public int pos;
    public Bundle bundle = new Bundle();
}
