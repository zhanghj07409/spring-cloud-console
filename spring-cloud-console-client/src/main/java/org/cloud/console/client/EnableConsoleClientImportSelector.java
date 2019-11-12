package org.cloud.console.client;

import org.cloud.console.client.autoconfigure.HealthAutoConfiguration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/11<br>
 * <br>
 */
public class EnableConsoleClientImportSelector implements DeferredImportSelector {
    public EnableConsoleClientImportSelector() {
    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{HealthAutoConfiguration.class.getCanonicalName()};
    }
}
