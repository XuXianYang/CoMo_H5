package com.dianxian.user.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;

public enum Encryption {
    MD5("MD5"), SHA("SHA");
    private String digestMethod;

    Encryption(String digestMethod) {
        this.digestMethod = digestMethod;
    }

    public String encryptAsHex(String content, String charset) {
        return Hex.encodeHexString(encrypt(content, charset));
    }
    
    public String encryptAsHex(String content) {
        return encryptAsHex(content, "UTF-8");
    }

    public String encryptAsBase64(String content) {
        return Base64.encodeBase64String(encrypt(content, "UTF-8"));
    }

    private byte[] encrypt(String content, String charset) {
        try {
            return MessageDigest.getInstance(this.digestMethod).digest(content.getBytes(charset));
        } catch (Exception e) {
            throw new RuntimeException("Failed to quickEncrypt: " + e.getMessage(), e);
        }
    }
}
