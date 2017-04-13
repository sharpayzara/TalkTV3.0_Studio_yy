package com.sumavision.talktv2.bean;

import java.util.List;

/**
 * 
 * @author liwei
 * @description 商城主页的实体bean
 * 
 */
public class ShoppingHomeBean {
	public int type;// 组显示类型，1=左侧大图，右侧两小图；2=三个小图
	public List<HotGoodsBean> hotGoods;// 推荐物品数组
}
