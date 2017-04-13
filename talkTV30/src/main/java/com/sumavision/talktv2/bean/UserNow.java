package com.sumavision.talktv2.bean;

import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.utils.DialogUtil;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * @author 郭鹏
 * @createTime 2012-5-31
 * @description 当前用户信息实体类
 * @changeLog
 */
public class UserNow {

	private static UserNow current;
	// 专区
	public long objectId = 0;
	public long zoneId = 0;// 当前专区id
	public int type = 0;
	public int way = 0;
	// 用户名
	public String name = "";
	// 昵称
	public String nickName = "";
	// 密码
	public String passwd = "123456";
	// 等级
	public String level;
	// 等级是否变化
	public int changeLevel;
	// 电子邮件
	public String eMail = "";
	public int userID = 0;
	// 登录状态
	public boolean isLogedIn = false;
	// 粉过节目数量
	public int favoriteCount = 0;
	// 粉友数量
	public int fansCount = 0;
	// 未读私信数量
	public int unreadMail = 0;
	// 私信数量
	public int mailCount = 0;
	// 用户头像URL
	public String iconURL = "";
	// 奖章数量
	public int badgeCount = 0;
	// 已取得的奖章数量
	public int badgesCount;
	public int channelCount;
	// 签到次数
	public int checkInCount = 0;
	// 评论次数
	public int commentCount = 0;
	// 用户最后查询的指定节目的评论的条数
	public int allCommentsCount;
	// 用户是否为登陆软件的用户
	public boolean isSelf = true;

	public String bitmapPath;

	// 用户积分变化值
	public int point;
	public int totalPoint;// 用户总积分
	public int diamond;// 用户钻石
	// 性别1=男2=女
	public int gender = 1;
	// IMEI
	public String imei;
	// 个性签名
	public String signature = "";

	// 好友事件数
	public int friendEventCount = 0;
	// 全部事件数
	public int eventCount = 0;

	// 从个人中心打开帮助
	public boolean isFromUserCenter2Help = false;

	public int privateMessageAllCount;
	// sessionID
	public String sessionID = "";
	public String jsession = "";
	// 当前拍的图片路径
	public String picPath = "";
	// 获得经验
	public int getExp = 0;
	// 总经验值
	public int exp = 0;
	// 升级
	public int lvlUp = 0;

	// <-------------------------复杂数据类型--------------------------------------
	// 与指定用户私信列表
	public List<CommentData> privateMessageList;
	// 私信收件箱列表
	public List<CommentData> privateMessageAllList;
	// 粉友列类表
	public List<User> fansList;
	// 私信
	public List<MailData> mail;
	// 签到
	public List<SignData> signList;
	// 招呼
	public List<MessageData> message;
	// 好友动态
	public List<User> friendEvent;
	// 关注数量
	public int friendCount;
	// 评论数量
	public int talkCount;
	// 追剧数量
	public int chaseCount;
	// 预约数量
	public int remindCount;
	// 被@数量
	public int atMeCount;
	// 被回复数量
	public int replyMeCount;
	// 关注列表
	public List<User> friend;
	// 追剧列表
	public List<ChaseData> chase;
	// 预约列表
	public List<VodProgramData> remind;
	// 领先多少用户
	public String badgeRate;
	// 账号类型 绑定的电视粉账号类型：1=注册新账号，2=已有老账号。
	public int userType = 1;
	// 第三方账号绑定映射Id
	public String thirdUserId;
	// 被@列表
	public List<CommentData> talkAtList;
	// 被回复列表
	public List<CommentData> replyList;
	// 新获得的徽章
	public List<BadgeData> newBadge;

	// 获得经验对应的操作
	public String action;
	public boolean showAlert;
	public String inviteCode="";
	public int vipIncPoint;
	public boolean isVip;
	public int dayLoterry = 0;

	public static UserNow current() {
		if (current == null) {
			current = new UserNow();
		}
		return current;
	}

	public void setTotalPoint(int newTotalPoint, int vipIncPoint) {
		if (action == null) {
			action = "";
		}
		String vipStr = "";
		if (vipIncPoint>0){
			vipStr = "VIP加成"+vipIncPoint+"积分";
		}
		EventMessage msg = new EventMessage("SlidingMainActivity");
		if (newTotalPoint > totalPoint) {
			msg.bundle.putString("message",action + " +"
					+ (newTotalPoint - totalPoint-vipIncPoint) + "积分" + vipStr);
			EventBus.getDefault().post(msg);
		} else if (newTotalPoint < totalPoint) {
			msg.bundle.putString("message",action + " "
					+ (newTotalPoint - totalPoint-vipIncPoint) + "积分" + vipStr);
			EventBus.getDefault().post(msg);
		}
		totalPoint = newTotalPoint;
		EventBus.getDefault().post(new UserInfoEvent());
	}
	public void setTotalPoint(int total){
		setTotalPoint(total, 0);
	}

}
