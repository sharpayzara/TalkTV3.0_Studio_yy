package com.sumavision.talktv2.bean;

import java.util.ArrayList;

/**
 * 兑换/领取礼品信息
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ExchangeGood {

	// 响应异常
	public static final int EXCHANGE_LESS_POINT = 2;
	public static final int EXCHANGE_LESS_GOODS = 3;
	public static final int EXCHANGE_LESS_DIAMOND = 4;
	public static final int EXCHANGE_NOT_START = 5;
	public static final int EXCHANGE_OVER = 6;

	public static final int STATUS_RECEIVED = 1;
	public static final int STATUS_OVER = 2;
	public static final int TYPE_VIRTUAL = 1;
	public static final int TYPE_ENTITY = 2;

	public long hotGoodsId;
	public long userGoodsId;// 用户-礼品映射id
	public int id;// 物品id
	public String pic;
	public String name;
	public int type;// 物品类型，1=电子劵，2=实物
	public int status;// 状态，1=已领取，2=已结束
	public String picDetail;
	public String startTime;
	public String currentTime;
	public String endTime;

	public String code;// 电子码
	public String useIntro;// 电子码使用说明
	public ArrayList<ReceiverInfo> userList;// 获奖名单

	public int point;// 积分
	public String intro;// 详细介绍/使用规则
	public int count;
	public String exchangeIntro;
	public ArrayList<String> picList;
	public ArrayList<ExchangeGood> contentList;
	public String content;
	public String title;
	
	public int fetchType;
	public int goodsTicketCount;
	public boolean ticket;
	public boolean isHolidayGoods;
	public int hasPieceCount;//拥有碎片数量
	public int totalPieceCount;//所需碎片总数
}
