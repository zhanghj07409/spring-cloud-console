package org.cloud.console.server.config;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import org.cloud.console.server.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/5/15<br>
 * <br>
 */
@Service("encryptablePropertyResolver")
public class MyEncryptablePropertyResolver implements EncryptablePropertyResolver {
    private static Logger log = LoggerFactory.getLogger(MyEncryptablePropertyResolver.class);
    @Autowired
    private EncryptablePropertyDetector encryptablePropertyDetector;
    private String key="ReVYSgILra77HDtSg+Or32jp/q03Ytpe";
    private String type="desede";
    //自定义解密方法
    @Override
    public String resolvePropertyValue(String s) {
        if (encryptablePropertyDetector.isEncrypted(s)) {
            try {
                switch (type){
                    case "base64":return new String(SecurityUtil.base64Decode(encryptablePropertyDetector.unwrapEncryptedValue(s)));
                    case "aes":return new String(SecurityUtil.aesDecode(key,SecurityUtil.base64Decode(encryptablePropertyDetector.unwrapEncryptedValue(s))));
                    case "des":return new String(SecurityUtil.desDecode(key,SecurityUtil.base64Decode(encryptablePropertyDetector.unwrapEncryptedValue(s))));
                    case "desede":return new String(SecurityUtil.deSedeDecode(key,SecurityUtil.base64Decode(encryptablePropertyDetector.unwrapEncryptedValue(s))));
                    default:return new String(SecurityUtil.base64Decode(encryptablePropertyDetector.unwrapEncryptedValue(s)));
                }
            } catch (Exception e) {
                log.error("配置解码异常",e);
                return s;
            }
        }
        return s;
    }
}
