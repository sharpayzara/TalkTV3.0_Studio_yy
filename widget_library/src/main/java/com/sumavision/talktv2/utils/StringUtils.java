package com.sumavision.talktv2.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String Utils
 * 
 */
public class StringUtils {

	/**
	 * is null or its length is 0 or it is made by space
	 * 
	 * <pre>
	 * isBlank(null) = true;
	 * isBlank(&quot;&quot;) = true;
	 * isBlank(&quot;  &quot;) = true;
	 * isBlank(&quot;a&quot;) = false;
	 * isBlank(&quot;a &quot;) = false;
	 * isBlank(&quot; a&quot;) = false;
	 * isBlank(&quot;a b&quot;) = false;
	 * </pre>
	 * 
	 * @param str
	 * @return if string is null or its size is 0 or it is made by space, return
	 *         true, else return false.
	 */
	public static boolean isBlank(String str) {
		return (str == null || str.trim().length() == 0);
	}

	public static boolean isNotEmpty(CharSequence cs) {
		return !isEmpty(cs);
	}

	/**
	 * is null or its length is 0
	 * 
	 * <pre>
	 * isEmpty(null) = true;
	 * isEmpty(&quot;&quot;) = true;
	 * isEmpty(&quot;  &quot;) = false;
	 * </pre>
	 * 
	 * @param str
	 * @return if string is null or its size is 0, return true, else return
	 *         false.
	 */
	public static boolean isEmpty(CharSequence str) {
		return (str == null || str.length() == 0);
	}

	/**
	 * null string to empty string
	 * 
	 * <pre>
	 * nullStrToEmpty(null) = &quot;&quot;;
	 * nullStrToEmpty(&quot;&quot;) = &quot;&quot;;
	 * nullStrToEmpty(&quot;aa&quot;) = &quot;aa&quot;;
	 * </pre>
	 * 
	 * @param str
	 * @return
	 */
	public static String nullStrToEmpty(String str) {
		return (str == null ? "" : str);
	}

	/**
	 * capitalize first letter
	 * 
	 * <pre>
	 * capitalizeFirstLetter(null)     =   null;
	 * capitalizeFirstLetter("")       =   "";
	 * capitalizeFirstLetter("2ab")    =   "2ab"
	 * capitalizeFirstLetter("a")      =   "A"
	 * capitalizeFirstLetter("ab")     =   "Ab"
	 * capitalizeFirstLetter("Abc")    =   "Abc"
	 * </pre>
	 * 
	 * @param str
	 * @return
	 */
	public static String capitalizeFirstLetter(String str) {
		if (isEmpty(str)) {
			return str;
		}

		char c = str.charAt(0);
		return (!Character.isLetter(c) || Character.isUpperCase(c)) ? str
				: new StringBuilder(str.length())
						.append(Character.toUpperCase(c))
						.append(str.substring(1)).toString();
	}

	/**
	 * encoded in utf-8
	 * 
	 * <pre>
	 * utf8Encode(null)        =   null
	 * utf8Encode("")          =   "";
	 * utf8Encode("aa")        =   "aa";
	 * utf8Encode("啊啊啊啊")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
	 * </pre>
	 * 
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 *             if an error occurs
	 */
	public static String utf8Encode(String str) {
		if (!isEmpty(str) && str.getBytes().length != str.length()) {
			try {
				return URLEncoder.encode(str, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						"UnsupportedEncodingException occurred. ", e);
			}
		}
		return str;
	}

	/**
	 * encoded in utf-8, if exception, return defultReturn
	 * 
	 * @param str
	 * @param defultReturn
	 * @return
	 */
	public static String utf8Encode(String str, String defultReturn) {
		if (!isEmpty(str) && str.getBytes().length != str.length()) {
			try {
				return URLEncoder.encode(str, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return defultReturn;
			}
		}
		return str;
	}

	public static boolean isEmail(String strEmail) {
		String strPattern = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	/**
	 * 把 汉字转换到 Unicode编码的内码格式。
	 * 
	 * @param strs
	 * @return 汉字转为Unicode编码
	 */
	public static String StrTOUnicode(String strs) {
		StringBuffer str = new StringBuffer();
		int len = strs.length();
		String[] s = new String[len];
		for (int i = 0; i < len; i++) {
			char c = strs.charAt(i);
			s[i] = Integer.toString(c, 16);
			str.append(s[i]);
		}
		return str.toString();
	}

	/**
	 * 汉字，英文，数字混合串转换为unicode
	 * 
	 * @param strs
	 * @return
	 */
	public static String AllStrTOUnicode(String strs) {
		StringBuffer str = new StringBuffer();
		int len = strs.length();
		String[] s = new String[len];
		for (int i = 0; i < len; i++) {
			char c = strs.charAt(i);

			if (c == 0xd) {
				str.append("000d");
			} else if (c == 0xa) {
				str.append("000a");
			}
			// 带标点符号的所有字符
			else if (c != 0xa && c != 0xd && c >= 0 && c <= '~') {
				s[i] = Integer.toString(c, 16);
				if (s[i].length() == 2) {
					str.append("00");
				} else if (s[i].length() == 3) {
					str.append("0");
				}
				str.append(s[i]);
			}
			// 是汉字
			else if (Character.isLetter(c)) {
				s[i] = Integer.toString(c, 16);
				if (s[i].length() == 2) {
					str.append("00");
				} else if (s[i].length() == 3) {
					str.append("0");
				}
				str.append(s[i]);
			} else {
				s[i] = Integer.toString(c, 16);
				if (s[i].length() == 2) {
					str.append("00");
				} else if (s[i].length() == 3) {
					str.append("0");
				}
				str.append(s[i]);
			}

		}
		return str.toString();
	}

	/**
	 * 把 Unicode编码的内码格式转换到 汉字。
	 * 
	 * @param strs
	 * @return
	 */
	public static String UnicodeTOStr(String strs) {
		StringBuffer str = new StringBuffer();
		int len = strs.length();
		String[] s = new String[len];
		s = strs.split("");
		for (int i = 1; i < s.length - 3; i += 4) {
			char c = (char) Integer.valueOf(
					s[i] + s[i + 1] + s[i + 2] + s[i + 3], 16).intValue();
			str.append(c);
		}
		return str.toString();
	}

	// 汉字转换为UTF-8编码方式
	public static String strToUTF8(String str) {
		if (str != null) {
			try {
				str = new String(str.trim().getBytes("ISO-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "";
			}
			return str;
		} else {
			return "";
		}
	}

	public static boolean containHanzi(String edInput) {
		Pattern p = Pattern.compile("[0-9]*");
		Matcher m = p.matcher(edInput);
		p = Pattern.compile("[\u4e00-\u9fa5]");
		m = p.matcher(edInput);
		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}
}
