package org.cloud.console.server.security;


import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/5/15<br>
 * <br>
 */
public class SecurityUtil {
    public static final String CHARSET_NAME="utf-8";

    /**
     * base64 加密
     * @param src
     * @return
     * @throws Exception
     */
    public static String base64Encode(byte[] src) throws Exception {
        byte[] encodeBytes =Base64.encodeBase64(src);
        return new String(encodeBytes,CHARSET_NAME);
    }

    /**
     * base64 加密
     * @param src
     * @return
     * @throws Exception
     */
    public static String base64Encode(String src) throws Exception {
        return base64Encode(src.getBytes(CHARSET_NAME));
    }
    /**
     * base64 解码
     * @param rst
     * @return
     * @throws Exception
     */
    public static byte[] base64Decode(byte[] rst) throws Exception {
        return Base64.decodeBase64(rst);
//        return new String(decodeBytes,CHARSET_NAME);
    }

    /**
     * base64 解码
     * @param rst
     * @return
     * @throws Exception
     */
    public static byte[] base64Decode(String rst) throws Exception {
        return base64Decode(rst.getBytes(CHARSET_NAME));
    }

    /**
     *  获取des key
     * @return
     * @throws Exception
     */
    public static String getDESKey() throws Exception {
        //使用BouncyCastle 的DES加密
        Security.addProvider(new BouncyCastleProvider());
        //生成密钥Key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES","BC");
        keyGenerator.init(56);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] bytesKey = secretKey.getEncoded();
        return base64Encode(bytesKey);
    }

    /**
     * des 加密
     * @param key
     * @param src
     * @return
     * @throws Exception
     */
    public static byte[] desEncode(String key,byte[] src) throws Exception {
        byte[] bytesKey=base64Decode(key);
        //KEY转换
        DESKeySpec deSedeKeySpec = new DESKeySpec(bytesKey);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
        Key convertSecretKey = factory.generateSecret(deSedeKeySpec);
        //加密
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, convertSecretKey);
        return  cipher.doFinal(src);
    }

    /**
     * des 解码
     * @param key
     * @param rst
     * @return
     * @throws Exception
     */
    public static byte[] desDecode(String key,byte[] rst) throws Exception {
        byte[] bytesKey=base64Decode(key);
        //KEY转换
        DESKeySpec deSedeKeySpec = new DESKeySpec(bytesKey);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
        Key convertSecretKey = factory.generateSecret(deSedeKeySpec);
        //解密
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE,convertSecretKey);
        return cipher.doFinal(rst);
    }

    /**
     *  获取3des key
     * @return
     * @throws Exception
     */
    public static String getDESedeKey() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        //生成密钥Key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede","BC");
        keyGenerator.getProvider();
        keyGenerator.init(168);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] bytesKey = secretKey.getEncoded();
        return base64Encode(bytesKey);
    }

    /**
     * 3des 加密
     * @param key
     * @param src
     * @return
     * @throws Exception
     */
    public static byte[] desSedeEncode(String key,byte[] src) throws Exception {
        byte[] bytesKey=base64Decode(key);
        //KEY转换
        DESedeKeySpec deSedeKeySpec = new DESedeKeySpec(bytesKey);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
        Key convertSecretKey = factory.generateSecret(deSedeKeySpec);
        //加密
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, convertSecretKey);
        return cipher.doFinal(src);
    }

    /**
     * 3des 解码
     * @param key
     * @param rst
     * @return
     * @throws Exception
     */
    public static byte[] deSedeDecode(String key,byte[] rst) throws Exception {
        byte[] bytesKey=base64Decode(key);
        //KEY转换
        DESedeKeySpec deSedeKeySpec = new DESedeKeySpec(bytesKey);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
        Key convertSecretKey = factory.generateSecret(deSedeKeySpec);
        //解密
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE,convertSecretKey);
        return  cipher.doFinal(rst);
    }


    /**
     *  获取AES key
     * @return
     * @throws Exception
     */
    public static String getAESKey() throws Exception {
        //使用BouncyCastle 的DES加密
        Security.addProvider(new BouncyCastleProvider());

        //生成Key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES","BC");
        keyGenerator.getProvider();
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] keyBytes = secretKey.getEncoded();
        return base64Encode(keyBytes);
    }

    /**
     * aes 加密
     * @param key
     * @param src
     * @return
     * @throws Exception
     */
    public static byte[] aesEncode(String key,byte[] src) throws Exception {
        byte[] bytesKey=base64Decode(key);
        //KEY转换
        Key aeskey = new SecretKeySpec(bytesKey, "AES");
        //加密
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, aeskey);
        return cipher.doFinal(src);
    }

    /**
     * aes 解码
     * @param key
     * @param rst
     * @return
     * @throws Exception
     */
    public static byte[] aesDecode(String key,byte[] rst) throws Exception {
        byte[] bytesKey=base64Decode(key);
        //KEY转换
        Key aeskey = new SecretKeySpec(bytesKey, "AES");
        //加密
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aeskey);
        return cipher.doFinal(rst);
    }


}
