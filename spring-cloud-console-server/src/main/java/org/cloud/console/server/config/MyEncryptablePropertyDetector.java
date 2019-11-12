package org.cloud.console.server.config;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import org.springframework.stereotype.Service;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/5/15<br>
 * <br>
 */
@Service("encryptablePropertyDetector")
public class MyEncryptablePropertyDetector implements EncryptablePropertyDetector {
    private String prefix="{mysecurity}";

    // 如果属性的字符开头为"{cipher}"，返回true，表明该属性是加密过的
    @Override
    public boolean isEncrypted(String s) {
        if (null != s) {
            return s.startsWith(prefix);
        }
        return false;
    }
    // 该方法告诉工具，如何将自定义前缀去除
    @Override
    public String unwrapEncryptedValue(String s) {
        return s.substring(prefix.length());
    }

    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
