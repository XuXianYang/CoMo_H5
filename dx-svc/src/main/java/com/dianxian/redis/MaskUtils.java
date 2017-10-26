package com.dianxian.redis;

public class MaskUtils {

	private static char MASK_CHAR = '*';

	/**
	 * Set global mask character
	 * 
	 * @param c
	 */
	public static final void setMaskChar(char c) {
		MASK_CHAR = c;
	}

	/**
	 * Mask the string from beginIdex(included) to endIndex(excluded)
	 * 
	 * @param str
	 * @param beginIndex included
	 * @param endIndex   excluded
	 * @return
	 */
	public static final String mask(String str, int beginIndex, int endIndex) {
		if (str == null) {
			return "";
		}

		StringBuilder buf = new StringBuilder(str.length());
		int length = str.length();
		int pos = 0;
		for (; pos < length; pos++) {
			if (pos >= beginIndex && pos < endIndex) {
				buf.append(MASK_CHAR);
			} else {
				buf.append(str.charAt(pos));
			}
		}

		return buf.toString();
	}

	/**
	 * Return the mask character string 
	 * 
	 * @param length
	 * @return
	 */
	public static final String mask(int length) {
		if (length <= 0) {
			return "";
		}

		StringBuilder buf = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			buf.append(MASK_CHAR);
		}
		return buf.toString();
	}

	/**
	 * Mask the string
	 * 
	 * @param str
	 * @return
	 */
	public static final String maskAll(String str) {
		if (str == null) {
			return "";
		} else {
			return mask(str.length());
		}
	}

	/**
	 * Mask name
	 * 
	 * @param name
	 * @return
	 */
	public static final String maskName(String name) {
		return mask(name, 0, 1);
	}

	/**
	 * Mask identify number
	 * 
	 * @param id
	 * @return
	 */
	public static final String maskID(String id) {
		if (id == null) {
			return "";
		} else {
			return mask(id, 0, id.length() - 4);
		}
	}
	
	/**
	 * Return the last number of identify number
	 * 
	 * @param id
	 * @return
	 */
	public static final String maskIDWithLastNum(String id) {
		if (id == null) {
			return "";
		} else if(id.length() < 5){
			return id;
		} else {
			return "尾号" + id.substring(id.length() - 4);
		}	
	}

	public static final String maskIDForLog(String id) {
		if (id == null) {
			return "";
		} else {
			return mask(id, 6, id.length() - 4);
		}
	}

	public static final String maskExternalID(String id) {
		if (id == null) {
			return "";
		} else {
			int len = id.length();
			int third = len / 3;
			return mask(id, third, len - third);
		}
	}

	public static final String maskMobile(String mobile) {
		if (mobile == null) {
			return "";
		} else {
			return mask(mobile, 3, mobile.length() - 4);
		}
	}

	public static final String maskEmail(String email) {
		if (email == null) {
			return "";
		} else {
			int pos = email.indexOf('@');
			String name = email;
			String domain = "";
			if (pos > 0) {
				name = email.substring(0, pos);
				domain = email.substring(pos);
			}
			return mask(name, 3, name.length()) + domain;
		}
	}
	
	public static final String maskBankCard(String card){
		if(card == null){
			return "";
		} else {
			return mask(card, 4, card.length() - 4);
		}
	}
	
	public static final String maskBankCardWithLastNum(String card){
		if(card == null){
			return "";
		} else if(card.length() < 5){
			return card;
		} else {
			return "尾号" + card.substring(card.length() - 4);
		}	
	}
	
	public static final String maskBankCardForLog(String card){
		if(card == null){
			return "";
		} else {
			return mask(card, 6, card.length() - 4);
		}	
	}
}
