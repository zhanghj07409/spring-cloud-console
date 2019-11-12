package org.cloud.console.server.security;

import org.junit.Test;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/5/15<br>
 * <br>
 */
public class SecurityUtilTest {
    private static final String CHARSET_NAME="utf-8";
    private static final String password="HHy123";

    @Test
    public void base64Test() throws Exception{
        System.out.println("========Baes64========");
        String en=SecurityUtil.base64Encode(password);
        System.out.println("key:");
        System.out.println("加密后内容:"+en);
        System.out.println("解密后内容:"+new String(SecurityUtil.base64Decode(en),CHARSET_NAME));
        System.out.println("========Baes64========");
    }
    @Test
    public void desTest() throws Exception{
        System.out.println();
        System.out.println("========des========");
        String key=SecurityUtil.getDESKey();
        System.out.println("key:"+key);
        byte[] src=SecurityUtil.desEncode(key,password.getBytes(CHARSET_NAME));
        String base=SecurityUtil.base64Encode(src);

        System.out.println("加密后内容:"+base);
        System.out.println("解密后内容:"+new String(SecurityUtil.desDecode(key,SecurityUtil.base64Decode(base))));
        System.out.println("========des========");
    }
    @Test
    public void desedeTest() throws Exception{
        System.out.println();
        System.out.println("========desede========");
        String key=SecurityUtil.getDESedeKey();
        System.out.println("key:"+key);
        byte[] src=SecurityUtil.desSedeEncode(key,password.getBytes(CHARSET_NAME));
        System.out.println("加密后内容:"+SecurityUtil.base64Encode(src));
        System.out.println("解密后内容:"+new String(SecurityUtil.deSedeDecode(key,src)));
        System.out.println("========desede========");
    }
    /*@Test()
    public void aesTest() throws Exception{
        System.out.println();
        System.out.println("========aes========");
        String key=SecurityUtil.getAESKey();
        System.out.println("key:"+key);
        byte[] src=SecurityUtil.aesEncode(key,password.getBytes(CHARSET_NAME));
        System.out.println("加密后内容:"+SecurityUtil.base64Encode(src));
        System.out.println("解密后内容:"+new String(SecurityUtil.aesDecode(key,src)));
        System.out.println("========aes========");
    }*/
}
