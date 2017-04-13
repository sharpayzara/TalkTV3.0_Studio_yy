package com.sumavision.talktv2.bean.interactive;

import com.google.gson.annotations.SerializedName;

/**
 * 参与竞猜结果
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class GuessResult {
	public boolean guessCorrect;
	public int userOptionId;
	public String userOptionName;
	public int prizeType;
	@SerializedName("guessQty")
	public int consumeCount;// 花费虚拟币数量
}
