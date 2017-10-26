package com.dianxian.session.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HmacUtils {

	private static final String KEY_MAC = "HmacSHA512";

	public static String initMacKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);
			SecretKey secretKey = keyGenerator.generateKey();
			return encryptBASE64(secretKey.getEncoded());
		} catch (NoSuchAlgorithmException e) {
//			Logger.error(HmacUtils.class, e.getMessage(), e);
		}
		return null;
	}

	public static byte[] encrypt(byte[] data, String key) {
		try {
			SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), KEY_MAC);
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			return mac.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
//			Logger.error(HmacUtils.class, e.getMessage(), e);
		} catch (InvalidKeyException e) {
//			Logger.error(HmacUtils.class, e.getMessage(), e);
		}
		return null;
	}
	
	public static String encryptAsString(byte[] data, String key) {
		return Base64.encodeBase64String(encrypt(data, key));
	}

    public static String encryptAsString(String data, String key) {
        try {
            return encryptAsString(data.getBytes("UTF-8"), key);
        } catch (UnsupportedEncodingException e) {
//            Logger.error(HmacUtils.class, e.getMessage(), e);
            return null;
        }
    }

	private static byte[] decryptBASE64(String key) {
		return Base64.decodeBase64(key);
	}

	private static String encryptBASE64(byte[] key) {
		return Base64.encodeBase64String(key);
	}

}