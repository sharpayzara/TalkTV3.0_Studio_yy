/*
 * 文 件 名:  ExchangeSecureUtil.java
 * 版    权:  北京数码视讯科技股份有限公司. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  黄有祥 40289
 * 修改时间:  2014-5-27
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package comsumavision.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apaches.commons.codec.binary.Base64;
import org.apaches.commons.codec.digest.DigestUtils;



import android.util.Log;

/**
 * 支付模块安全工具类
 * 
 * @author 黄有祥 40289
 * @version [版本号, 2014-5-27]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
/**
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  黄有祥 40289
 * @version  [版本号, 2014-5-29]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author 黄有祥 40289
 * @version [版本号, 2014-5-29]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
/**
 * 
 * @author zhaohongru xiugai
 */

public class ExchangeSecureUtils {

	/**
	 * 生成种子
	 * 
	 * @return 21bits string
	 */
	public static String genSeed() {
		return "" + randomInt(1000, 1999) + System.currentTimeMillis()
				+ randomInt(3000, 9999);
	}

	private static int randomInt(int min, int max) {
		// TODO Auto-generated method stub
		return (int) Math.round(Math.random() * (max - min) + min);
	}

	// 生成加密种子
	public static String encryptSeed(String seed) {
		byte[] bytes = DigestUtils.md5(seed);
		byte[] datas = new byte[8];
		for (int i = 4; i < datas.length; i++) {
			datas[11 - i] = bytes[i];
		}
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(datas);
		DataInputStream dataInputStream = new DataInputStream(byteInputStream);
		long val = 0;
		try {
			val = dataInputStream.readLong();
		} catch (IOException e) {
			Log.d("t", e.toString());
		}
		final String source = "abcdefghijklmnopq!@#$%^&*rstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ/\\:._-1234567890";
		final int len = 16;
		StringBuilder mixed = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			val = (val * 189 + 30021) % 0xffff;
			int index = (int) (val * source.length() >> 16);
			mixed.append(source.charAt(index));
		}
		return new String(mixed);
	}

	public static final String ALGORITHM = "AES";

	// AES-ecb加密算法要求：
	// 待加密内容的长度必须是16的倍数
	// 密钥必须是16位的
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	/**
	 * 明文加密
	 * 
	 * @param text
	 * @param seed
	 * @return
	 */
	public  String textEncrypt(String text, String seed) {
		SecretKey key = new SecretKeySpec(seed.getBytes(), ALGORITHM);
		byte[] result = null;
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			// TODO.. charset是否需要？？
			result = cipher.doFinal(text.getBytes());
		} catch (NoSuchAlgorithmException e) {
			Log.d("t", e.toString());
		} catch (NoSuchPaddingException e) {
			Log.d("t", e.toString());
		} catch (InvalidKeyException e) {
			Log.d("t", e.toString());
		} catch (IllegalBlockSizeException e) {
			Log.d("t", e.toString());
		} catch (BadPaddingException e) {
			Log.d("t", e.toString());
		}
		return Base64.encodeBase64String(result);
	}

	// 密文解密
	public  String textDecrypt(String text, String seed) {
		SecretKey key = new SecretKeySpec(seed.getBytes(), ALGORITHM);
		byte[] result = null;
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			result = cipher.doFinal(Base64.decodeBase64(text));
		} catch (NoSuchAlgorithmException e) {
			Log.d("t", e.toString());
		} catch (NoSuchPaddingException e) {
			Log.d("t", e.toString());
		} catch (InvalidKeyException e) {
			Log.d("t", e.toString());
		} catch (IllegalBlockSizeException e) {
			Log.d("t", e.toString());
		} catch (BadPaddingException e) {
			Log.d("t", e.toString());
		}
		return new String(result);
	}

	public static void main(String[] args) {
		
	}
}
