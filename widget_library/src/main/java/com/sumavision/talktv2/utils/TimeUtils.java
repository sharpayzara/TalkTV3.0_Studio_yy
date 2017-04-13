package com.sumavision.talktv2.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TimeUtils
 * <ul>
 * format date
 * <li>{@link #getMonthDay(Date)}</li>
 * </ul>
 * <ul>
 * 
 * @author suma-hpb
 * 
 */
public class TimeUtils {

	/**
	 * 
	 * @param time
	 *            格式为yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static long StringToMillSeconds(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = format.parse(time);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0l;
	}

	/**
	 * return "MM-dd HH:mm"
	 * 
	 * @param date
	 * @return
	 */
	public static String getMonthDay(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		return sdf.format(date);
	}
}
