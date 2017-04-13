package com.sumavision.talktv2.bean;

import java.io.Serializable;

public class SegInfo implements Serializable
{
    private static final long serialVersionUID = 1L;
    public int index;// 文件坐标
    public String downloadUrl;// 文件地址
    public String timeLength;// 文件时长
    public long dataLength;// 文件长度
    public boolean isDownloaded = false; //是否下载完毕
    public long breakPoint; //断点
}
